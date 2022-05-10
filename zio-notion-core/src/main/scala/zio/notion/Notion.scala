package zio.notion

import io.circe.Decoder
import io.circe.parser.decode

import zio._
import zio.notion.NotionClient.NotionResponse
import zio.notion.NotionError.JsonError
import zio.notion.model.database.Database
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

  def updatePage(patch: Page.Patch): IO[NotionError, Page]
}

object Notion {
  def retrievePage(pageId: String): ZIO[Notion, NotionError, Page]             = ZIO.service[Notion].flatMap(_.retrievePage(pageId))
  def retrieveDatabase(databaseId: String): ZIO[Notion, NotionError, Database] = ZIO.service[Notion].flatMap(_.retrieveDatabase(databaseId))
  def retrieveUser(userId: String): ZIO[Notion, NotionError, User]             = ZIO.service[Notion].flatMap(_.retrieveUser(userId))

  def updatePage(patch: Page.Patch): ZIO[Notion, NotionError, Page] = ZIO.service[Notion].flatMap(_.updatePage(patch))

  val live: URLayer[NotionClient, Notion] = ZLayer(ZIO.service[NotionClient].map(LiveNotion))

  final case class LiveNotion(notionClient: NotionClient) extends Notion {
    private def decodeResponse[T: Decoder](request: IO[NotionError, NotionResponse]): IO[NotionError, T] = request.flatMap(decodeJson[T])

    override def retrievePage(pageId: String): IO[NotionError, Page] = decodeResponse[Page](notionClient.retrievePage(pageId))
    override def retrieveDatabase(databaseId: String): IO[NotionError, Database] =
      decodeResponse[Database](notionClient.retrieveDatabase(databaseId))
    override def retrieveUser(userId: String): IO[NotionError, User] = decodeResponse[User](notionClient.retrieveUser(userId))

    override def updatePage(patch: Page.Patch): IO[NotionError, Page] = decodeResponse[Page](notionClient.updatePage(patch))
  }
}
