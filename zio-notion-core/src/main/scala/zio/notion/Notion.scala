package zio.notion

import io.circe.Decoder
import io.circe.parser.decode
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend

import zio._
import zio.notion.NotionClient.NotionResponse
import zio.notion.NotionError.JsonError
import zio.notion.model.common.{Cover, Icon}
import zio.notion.model.common.richtext.RichTextData
import zio.notion.model.database.{Database, DatabaseQuery}
import zio.notion.model.database.PropertyDefinitionPatch.PropertySchema
import zio.notion.model.database.query.{Filter, Query, Sorts}
import zio.notion.model.page.Page
import zio.notion.model.user.User

sealed trait Notion {
  protected def decodeJson[T: Decoder](content: String): IO[NotionError, T] =
    decode[T](content) match {
      case Right(t)    => ZIO.succeed(t)
      case Left(error) => ZIO.fail(JsonError(error))
    }

  def retrievePage(pageId: String): IO[NotionError, Page]
  def retrieveDatabase(databaseId: String): IO[NotionError, Database]
  def retrieveUser(userId: String): IO[NotionError, User]

  def queryDatabase(databaseId: String, query: Query): IO[NotionError, DatabaseQuery]

  def updatePage(patch: Page.Patch): IO[NotionError, Page]
  def updateDatabase(patch: Database.Patch): IO[NotionError, Database]

  def createDatabase(
      pageId: String,
      title: Seq[RichTextData],
      icon: Option[Icon],
      cover: Option[Cover],
      properties: Map[String, PropertySchema]
  ): IO[NotionError, Database]
}

object Notion {
  def retrievePage(pageId: String): ZIO[Notion, NotionError, Page]             = ZIO.service[Notion].flatMap(_.retrievePage(pageId))
  def retrieveDatabase(databaseId: String): ZIO[Notion, NotionError, Database] = ZIO.service[Notion].flatMap(_.retrieveDatabase(databaseId))
  def retrieveUser(userId: String): ZIO[Notion, NotionError, User]             = ZIO.service[Notion].flatMap(_.retrieveUser(userId))

  def queryDatabase(databaseId: String, query: Query): ZIO[Notion, NotionError, DatabaseQuery] =
    ZIO.service[Notion].flatMap(_.queryDatabase(databaseId, query))
  def queryDatabase(databaseId: String): ZIO[Notion, NotionError, DatabaseQuery] = queryDatabase(databaseId, Query(None, None))
  def queryDatabase(databaseId: String, filter: Filter, sorts: Sorts): ZIO[Notion, NotionError, DatabaseQuery] =
    queryDatabase(databaseId, Query(Some(filter), Some(sorts)))
  def queryDatabase(databaseId: String, sorts: Sorts): ZIO[Notion, NotionError, DatabaseQuery] =
    queryDatabase(databaseId, Query(None, Some(sorts)))
  def queryDatabase(databaseId: String, filter: Filter): ZIO[Notion, NotionError, DatabaseQuery] =
    queryDatabase(databaseId, Query(Some(filter), None))

  def updatePage(patch: Page.Patch): ZIO[Notion, NotionError, Page]             = ZIO.service[Notion].flatMap(_.updatePage(patch))
  def updateDatabase(patch: Database.Patch): ZIO[Notion, NotionError, Database] = ZIO.service[Notion].flatMap(_.updateDatabase(patch))

  def createDatabase(
      pageId: String,
      title: Seq[RichTextData],
      icon: Option[Icon],
      cover: Option[Cover],
      properties: Map[String, PropertySchema]
  ): ZIO[Notion, NotionError, Database] = ZIO.service[Notion].flatMap(_.createDatabase(pageId, title, icon, cover, properties))

  val live: URLayer[NotionClient, Notion] = ZLayer(ZIO.service[NotionClient].map(LiveNotion))

  def layerWith(bearer: String): Layer[Throwable, Notion] =
    AsyncHttpClientZioBackend.layer() ++ ZLayer.succeed(NotionConfiguration(bearer)) >>> NotionClient.live >>> Notion.live

  final case class LiveNotion(notionClient: NotionClient) extends Notion {
    private def decodeResponse[T: Decoder](request: IO[NotionError, NotionResponse]): IO[NotionError, T] = request.flatMap(decodeJson[T])

    override def retrievePage(pageId: String): IO[NotionError, Page] = decodeResponse[Page](notionClient.retrievePage(pageId))
    override def retrieveDatabase(databaseId: String): IO[NotionError, Database] =
      decodeResponse[Database](notionClient.retrieveDatabase(databaseId))
    override def retrieveUser(userId: String): IO[NotionError, User] = decodeResponse[User](notionClient.retrieveUser(userId))

    override def queryDatabase(databaseId: String, query: Query): IO[NotionError, DatabaseQuery] =
      decodeResponse[DatabaseQuery](notionClient.queryDatabase(databaseId, query))

    override def updatePage(patch: Page.Patch): IO[NotionError, Page] = decodeResponse[Page](notionClient.updatePage(patch))
    override def updateDatabase(patch: Database.Patch): IO[NotionError, Database] =
      decodeResponse[Database](notionClient.updateDatabase(patch))

    override def createDatabase(
        pageId: String,
        title: Seq[RichTextData],
        icon: Option[Icon],
        cover: Option[Cover],
        properties: Map[String, PropertySchema]
    ): IO[NotionError, Database] = decodeResponse[Database](notionClient.createDatabase(pageId, title, icon, cover, properties))
  }
}
