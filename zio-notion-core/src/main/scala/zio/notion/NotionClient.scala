package zio.notion

import io.circe.{Decoder, Json}
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import sttp.client3._
import sttp.client3.asynchttpclient.zio.SttpClient
import sttp.model.Uri

import zio._
import zio.notion.NotionClient.NotionResponse
import zio.notion.NotionError._
import zio.notion.model.common.{Cover, Icon}
import zio.notion.model.common.Parent.{DatabaseId, PageId}
import zio.notion.model.common.richtext.RichTextData
import zio.notion.model.database.Database
import zio.notion.model.database.PatchedPropertyDefinition.PropertySchema
import zio.notion.model.database.query.Query
import zio.notion.model.page.{Page, PatchedProperty}
import zio.notion.model.printer

trait NotionClient {
  def retrievePage(pageId: String): IO[NotionError, NotionResponse]
  def retrieveDatabase(databaseId: String): IO[NotionError, NotionResponse]
  def retrieveUser(userId: String): IO[NotionError, NotionResponse]
  def retrieveUsers(pagination: Pagination): IO[NotionError, NotionResponse]

  def queryDatabase(databaseId: String, query: Query, pagination: Pagination): IO[NotionError, NotionResponse]

  def updatePage(pageId: String, operations: Page.Patch.StatelessOperations): IO[NotionError, NotionResponse]
  def updatePage(page: Page, operations: Page.Patch.Operations): IO[NotionError, NotionResponse]

  def updateDatabase(databaseId: String, operations: Database.Patch.StatelessOperations): IO[NotionError, NotionResponse]
  def updateDatabase(database: Database, operations: Database.Patch.Operations): IO[NotionError, NotionResponse]

  def createDatabase(
      pageId: String,
      title: Seq[RichTextData],
      icon: Option[Icon],
      cover: Option[Cover],
      properties: Map[String, PropertySchema]
  ): IO[NotionError, NotionResponse]

  def createPage(
      parent: PageId,
      title: Option[PatchedProperty],
      icon: Option[Icon],
      cover: Option[Cover]
  ): IO[NotionError, NotionResponse]

  def createPageInDatabase(
      parent: DatabaseId,
      properties: Map[String, PatchedProperty],
      icon: Option[Icon],
      cover: Option[Cover]
  ): IO[NotionError, NotionResponse]
}

object NotionClient {

  def retrievePage(pageId: String): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.service[NotionClient].flatMap(_.retrievePage(pageId))

  def retrieveDatabase(databaseId: String): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.service[NotionClient].flatMap(_.retrieveDatabase(databaseId))

  def retrieveUser(userId: String): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.service[NotionClient].flatMap(_.retrieveUser(userId))

  def retrieveUsers(pagination: Pagination): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.service[NotionClient].flatMap(_.retrieveUsers(pagination))

  def queryDatabase(databaseId: String, query: Query, pagination: Pagination): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.service[NotionClient].flatMap(_.queryDatabase(databaseId, query, pagination))

  def updatePage(pageId: String, operations: Page.Patch.StatelessOperations): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.service[NotionClient].flatMap(_.updatePage(pageId, operations))

  def updatePage(page: Page, operations: Page.Patch.Operations): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.service[NotionClient].flatMap(_.updatePage(page, operations))

  def updateDatabase(databaseId: String, operations: Database.Patch.StatelessOperations): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.service[NotionClient].flatMap(_.updateDatabase(databaseId, operations))

  def updateDatabase(database: Database, operations: Database.Patch.Operations): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.service[NotionClient].flatMap(_.updateDatabase(database, operations))

  type NotionResponse = String

  final case class NotionClientError(
      status:  Int,
      code:    String,
      message: String
  )

  object NotionClientError {
    implicit val decoder: Decoder[NotionClientError] = deriveDecoder[NotionClientError]
  }

  val live: URLayer[NotionConfiguration with SttpClient, NotionClient] =
    ZLayer {
      for {
        config     <- ZIO.service[NotionConfiguration]
        sttpClient <- ZIO.service[SttpClient]
      } yield LiveNotionClient(config, sttpClient)
    }

  final case class LiveNotionClient(config: NotionConfiguration, sttpClient: SttpClient) extends NotionClient {
    val endpoint: Uri = uri"https://api.notion.com/v1"

    implicit private class RequestOps(request: Request[Either[String, String], Any]) {

      def handle: IO[NotionError, NotionResponse] =
        sttpClient
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
    }

    private def defaultRequest: RequestT[Empty, Either[String, String], Any] =
      basicRequest.auth
        .bearer(config.bearer)
        .header("Notion-Version", "2022-02-22")
        .header("Content-Type", "application/json")

    override def retrievePage(pageId: String): IO[NotionError, NotionResponse] =
      defaultRequest
        .get(uri"$endpoint/pages/$pageId")
        .handle

    override def retrieveDatabase(databaseId: String): IO[NotionError, NotionResponse] =
      defaultRequest
        .get(uri"$endpoint/databases/$databaseId")
        .handle

    override def retrieveUser(userId: String): IO[NotionError, NotionResponse] =
      defaultRequest
        .get(uri"$endpoint/users/$userId")
        .handle

    override def retrieveUsers(pagination: Pagination): IO[NotionError, NotionResponse] =
      defaultRequest
        .get(uri"$endpoint/users")
        .body(printer.print(pagination.asJson))
        .handle

    override def queryDatabase(databaseId: String, query: Query, pagination: Pagination): IO[NotionError, NotionResponse] =
      defaultRequest
        .post(uri"$endpoint/databases/$databaseId/query")
        .body(printer.print(query.asJson deepMerge pagination.asJson))
        .handle

    override def updatePage(pageId: String, operations: Page.Patch.StatelessOperations): IO[NotionError, NotionResponse] = {
      val patch = Page.Patch.empty.setOperations(operations)

      defaultRequest
        .patch(uri"$endpoint/pages/$pageId")
        .body(printer.print(patch.asJson))
        .handle
    }

    override def updatePage(page: Page, operations: Page.Patch.Operations): IO[NotionError, NotionResponse] =
      for {
        patch <- ZIO.fromEither(Page.Patch.empty.updateOperations(page, operations))
        response <-
          defaultRequest
            .patch(uri"$endpoint/pages/${page.id}")
            .body(printer.print(patch.asJson))
            .handle
      } yield response

    override def updateDatabase(databaseId: String, operations: Database.Patch.StatelessOperations): IO[NotionError, NotionResponse] = {
      val patch = Database.Patch.empty.setOperations(operations)

      defaultRequest
        .patch(uri"$endpoint/databases/$databaseId")
        .body(printer.print(patch.asJson))
        .handle
    }

    override def updateDatabase(database: Database, operations: Database.Patch.Operations): IO[NotionError, NotionResponse] =
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
        title: Seq[RichTextData],
        icon: Option[Icon],
        cover: Option[Cover],
        properties: Map[String, PropertySchema]
    ): IO[NotionError, NotionResponse] = {
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
        cover: Option[Cover]
    ): IO[NotionError, NotionResponse] = {
      val json =
        Json.obj(
          "parent"     -> parent.asJson,
          "properties" -> properties.asJson,
          "icon"       -> icon.asJson,
          "cover"      -> cover.asJson
        )

      defaultRequest
        .post(uri"$endpoint/pages/")
        .body(printer.print(json))
        .handle
    }

    override def createPage(
        parent: PageId,
        title: Option[PatchedProperty],
        icon: Option[Icon],
        cover: Option[Cover]
    ): IO[NotionError, NotionResponse] = {
      val json =
        Json.obj(
          "parent"     -> parent.asJson,
          "properties" -> Json.obj("title" -> title.asJson),
          "icon"       -> icon.asJson,
          "cover"      -> cover.asJson
        )

      defaultRequest
        .post(uri"$endpoint/pages/")
        .body(printer.print(json))
        .handle
    }

  }
}
