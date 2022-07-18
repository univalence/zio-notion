package zio.notion

import io.circe.{Decoder, Json}
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import sttp.client3._
import sttp.model.Uri

import zio.{IO, _}
import zio.notion.NotionClient.NotionResponse
import zio.notion.NotionError._
import zio.notion.model.block.BlockContent
import zio.notion.model.common.{Cover, Icon}
import zio.notion.model.common.Parent.{DatabaseId, PageId}
import zio.notion.model.common.richtext.RichTextFragment
import zio.notion.model.database.Database
import zio.notion.model.database.PatchedPropertyDefinition.PropertySchema
import zio.notion.model.database.query.Query
import zio.notion.model.page.{Page, PatchedProperty}
import zio.notion.model.printer

trait NotionClient {
  def retrievePage(pageId: String)(implicit trace: Trace): IO[NotionError, NotionResponse]
  def retrieveDatabase(databaseId: String)(implicit trace: Trace): IO[NotionError, NotionResponse]
  def retrieveUser(userId: String)(implicit trace: Trace): IO[NotionError, NotionResponse]
  def retrieveUsers(pagination: Pagination)(implicit trace: Trace): IO[NotionError, NotionResponse]
  def retrieveBlock(blockId: String)(implicit trace: Trace): IO[NotionError, NotionResponse]
  def retrieveBlocks(pageId: String, pagination: Pagination)(implicit trace: Trace): IO[NotionError, NotionResponse]

  def queryDatabase(databaseId: String, query: Query, pagination: Pagination)(implicit trace: Trace): IO[NotionError, NotionResponse]

  def updatePage(pageId: String, operations: Page.Patch.StatelessOperations)(implicit trace: Trace): IO[NotionError, NotionResponse]
  def updatePage(page: Page, operations: Page.Patch.Operations)(implicit trace: Trace): IO[NotionError, NotionResponse]

  def updateDatabase(
      databaseId: String,
      operations: Database.Patch.StatelessOperations
  )(implicit trace: Trace): IO[NotionError, NotionResponse]
  def updateDatabase(database: Database, operations: Database.Patch.Operations)(implicit trace: Trace): IO[NotionError, NotionResponse]

  def createDatabase(
      pageId: String,
      title: Seq[RichTextFragment],
      icon: Option[Icon],
      cover: Option[Cover],
      properties: Map[String, PropertySchema]
  )(implicit trace: Trace): IO[NotionError, NotionResponse]

  def createPageInPage(
      parent: PageId,
      title: Option[PatchedProperty],
      icon: Option[Icon],
      cover: Option[Cover],
      children: Seq[BlockContent]
  )(implicit trace: Trace): IO[NotionError, NotionResponse]

  def createPageInDatabase(
      parent: DatabaseId,
      properties: Map[String, PatchedProperty],
      icon: Option[Icon],
      cover: Option[Cover],
      children: Seq[BlockContent]
  )(implicit trace: Trace): IO[NotionError, NotionResponse]
}

object NotionClient {

  def retrievePage(pageId: String)(implicit trace: Trace): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.serviceWithZIO[NotionClient](_.retrievePage(pageId))

  def retrieveDatabase(databaseId: String)(implicit trace: Trace): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.serviceWithZIO[NotionClient](_.retrieveDatabase(databaseId))

  def retrieveUser(userId: String)(implicit trace: Trace): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.serviceWithZIO[NotionClient](_.retrieveUser(userId))

  def retrieveUsers(pagination: Pagination)(implicit trace: Trace): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.serviceWithZIO[NotionClient](_.retrieveUsers(pagination))

  def queryDatabase(
      databaseId: String,
      query: Query,
      pagination: Pagination
  )(implicit trace: Trace): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.serviceWithZIO[NotionClient](_.queryDatabase(databaseId, query, pagination))

  def updatePage(
      pageId: String,
      operations: Page.Patch.StatelessOperations
  )(implicit trace: Trace): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.serviceWithZIO[NotionClient](_.updatePage(pageId, operations))

  def updatePage(page: Page, operations: Page.Patch.Operations)(implicit trace: Trace): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.serviceWithZIO[NotionClient](_.updatePage(page, operations))

  def updateDatabase(
      databaseId: String,
      operations: Database.Patch.StatelessOperations
  )(implicit trace: Trace): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.serviceWithZIO[NotionClient](_.updateDatabase(databaseId, operations))

  def updateDatabase(
      database: Database,
      operations: Database.Patch.Operations
  )(implicit trace: Trace): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.serviceWithZIO[NotionClient](_.updateDatabase(database, operations))

  type NotionResponse = String

  final case class NotionClientError(
      status:  Int,
      code:    String,
      message: String
  )

  object NotionClientError {
    implicit val decoder: Decoder[NotionClientError] = deriveDecoder[NotionClientError]
  }

  val live: URLayer[NotionConfiguration with Backend, NotionClient] =
    ZLayer {
      for {
        config  <- ZIO.service[NotionConfiguration]
        backend <- ZIO.service[Backend]
      } yield LiveNotionClient(config, backend)
    }

  case class LiveNotionClient(config: NotionConfiguration, backend: Backend) extends NotionClient { // scalafix:ok
    val endpoint: Uri = uri"https://api.notion.com/v1"

    def apply(request: NotionRequest): IO[NotionError, NotionResponse] =
      backend
        .send(request)
        .mapError(t => ConnectionError(t))
        .flatMap(response =>
          response.code match {
            case code if code.isSuccess => ZIO.succeed(response.body.merge)
            case _ =>
              val error =
                decode[NotionClientError](response.body.merge) match {
                  case Left(error)  => JsonError(error)
                  case Right(error) => NotionError.HttpError(request.toCurl, error.status, error.code, error.message)
                }

              ZIO.fail(error)
          }
        )

    implicit private class RequestOps(request: NotionRequest) {
      def handle: IO[NotionError, NotionResponse] = apply(request)
    }

    private def defaultRequest: RequestT[Empty, Either[String, String], Any] =
      basicRequest.auth
        .bearer(config.bearer)
        .header("Notion-Version", "2022-02-22")
        .header("Content-Type", "application/json")

    override def retrievePage(pageId: String)(implicit trace: Trace): IO[NotionError, NotionResponse] =
      defaultRequest
        .get(uri"$endpoint/pages/$pageId")
        .handle

    override def retrieveDatabase(databaseId: String)(implicit trace: Trace): IO[NotionError, NotionResponse] =
      defaultRequest
        .get(uri"$endpoint/databases/$databaseId")
        .handle

    override def retrieveUser(userId: String)(implicit trace: Trace): IO[NotionError, NotionResponse] =
      defaultRequest
        .get(uri"$endpoint/users/$userId")
        .handle

    override def retrieveUsers(pagination: Pagination)(implicit trace: Trace): IO[NotionError, NotionResponse] =
      defaultRequest
        .get(uri"$endpoint/users")
        .body(printer.print(pagination.asJson))
        .handle

    override def retrieveBlock(blockId: String)(implicit trace: Trace): IO[NotionError, NotionResponse] =
      defaultRequest
        .get(uri"$endpoint/blocks/$blockId")
        .handle

    override def retrieveBlocks(pageId: String, pagination: Pagination)(implicit trace: Trace): IO[NotionError, NotionResponse] =
      defaultRequest
        .get(uri"$endpoint/blocks/$pageId/children")
        .body(printer.print(pagination.asJson))
        .handle

    override def queryDatabase(databaseId: String, query: Query, pagination: Pagination)(implicit
        trace: Trace
    ): IO[NotionError, NotionResponse] =
      defaultRequest
        .post(uri"$endpoint/databases/$databaseId/query")
        .body(printer.print(query.asJson deepMerge pagination.asJson))
        .handle

    override def updatePage(pageId: String, operations: Page.Patch.StatelessOperations)(implicit
        trace: Trace
    ): IO[NotionError, NotionResponse] = {
      val patch = Page.Patch.empty.setOperations(operations)

      defaultRequest
        .patch(uri"$endpoint/pages/$pageId")
        .body(printer.print(patch.asJson))
        .handle
    }

    override def updatePage(page: Page, operations: Page.Patch.Operations)(implicit trace: Trace): IO[NotionError, NotionResponse] =
      for {
        patch <- ZIO.fromEither(Page.Patch.empty.updateOperations(page, operations))
        response <-
          defaultRequest
            .patch(uri"$endpoint/pages/${page.id}")
            .body(printer.print(patch.asJson))
            .handle
      } yield response

    override def updateDatabase(databaseId: String, operations: Database.Patch.StatelessOperations)(implicit
        trace: Trace
    ): IO[NotionError, NotionResponse] = {
      val patch = Database.Patch.empty.setOperations(operations)

      defaultRequest
        .patch(uri"$endpoint/databases/$databaseId")
        .body(printer.print(patch.asJson))
        .handle
    }

    override def updateDatabase(database: Database, operations: Database.Patch.Operations)(implicit
        trace: Trace
    ): IO[NotionError, NotionResponse] =
      for {
        patch <- ZIO.fromEither(Database.Patch.empty.updateOperations(database, operations))
        response <-
          defaultRequest
            .patch(uri"$endpoint/databases/${database.id}")
            .body(printer.print(patch.asJson))
            .handle
      } yield response

    override def createDatabase(
        pageId: String,
        title: Seq[RichTextFragment],
        icon: Option[Icon],
        cover: Option[Cover],
        properties: Map[String, PropertySchema]
    )(implicit trace: Trace): IO[NotionError, NotionResponse] = {
      val json =
        Json.obj(
          "parent" -> Json.obj(
            "type"    -> "page_id".asJson,
            "page_id" -> pageId.asJson
          ),
          "title"      -> title.asJson,
          "icon"       -> icon.asJson,
          "cover"      -> cover.asJson,
          "properties" -> properties.asJson
        )

      defaultRequest
        .post(uri"$endpoint/databases/")
        .body(printer.print(json))
        .handle
    }

    override def createPageInDatabase(
        parent: DatabaseId,
        properties: Map[String, PatchedProperty],
        icon: Option[Icon],
        cover: Option[Cover],
        children: Seq[BlockContent]
    )(implicit trace: Trace): IO[NotionError, NotionResponse] = {
      val json =
        Json.obj(
          "parent"     -> parent.asJson,
          "properties" -> properties.asJson,
          "icon"       -> icon.asJson,
          "cover"      -> cover.asJson,
          "children"   -> children.asJson
        )
      defaultRequest
        .post(uri"$endpoint/pages/")
        .body(printer.print(json))
        .handle
    }

    override def createPageInPage(
        parent: PageId,
        title: Option[PatchedProperty],
        icon: Option[Icon],
        cover: Option[Cover],
        children: Seq[BlockContent]
    )(implicit trace: Trace): IO[NotionError, NotionResponse] = {
      val json =
        Json.obj(
          "parent"     -> parent.asJson,
          "properties" -> Json.obj("title" -> title.asJson),
          "icon"       -> icon.asJson,
          "cover"      -> cover.asJson,
          "children"   -> children.asJson
        )

      defaultRequest
        .post(uri"$endpoint/pages/")
        .body(printer.print(json))
        .handle
    }
  }
}
