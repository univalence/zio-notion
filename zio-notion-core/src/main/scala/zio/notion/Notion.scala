package zio.notion

import io.circe.Decoder
import io.circe.parser.decode

import zio._
import zio.notion.NotionClient.NotionResponse
import zio.notion.model.database.Database
import zio.notion.model.page.Page
import zio.notion.model.user.User

import java.util.UUID

sealed trait Notion {
  protected def decodeJson[T: Decoder](content: String): IO[NotionError, T] =
    decode[T](content) match {
      case Right(t)    => ZIO.succeed(t)
      case Left(error) => ZIO.fail(JsonError(error))
    }

  def retrievePage(pageId: UUID): IO[NotionError, Page]
  def retrieveDatabase(databaseId: UUID): IO[NotionError, Database]
  def retrieveUser(userId: UUID): IO[NotionError, User]

  def updatePage(patch: Page.Patch): IO[NotionError, Page]
}

object Notion {
  def retrievePage(pageId: UUID): ZIO[Notion, NotionError, Page]             = ZIO.service[Notion].flatMap(_.retrievePage(pageId))
  def retrieveDatabase(databaseId: UUID): ZIO[Notion, NotionError, Database] = ZIO.service[Notion].flatMap(_.retrieveDatabase(databaseId))
  def retrieveUser(userId: UUID): ZIO[Notion, NotionError, User]             = ZIO.service[Notion].flatMap(_.retrieveUser(userId))

  def updatePage(patch: Page.Patch): ZIO[Notion, NotionError, Page] = ZIO.service[Notion].flatMap(_.updatePage(patch))

  val live: URLayer[NotionClient, Notion] = ZLayer(ZIO.service[NotionClient].map(LiveNotion))

  final case class LiveNotion(notionClient: NotionClient) extends Notion {
    private def decodeResponse[T: Decoder](request: IO[NotionError, NotionResponse]): IO[NotionError, T] =
      request
        .flatMap(response =>
          response.code match {
            case code if code.isSuccess => ZIO.succeed(response.body.merge)
            case code                   => ZIO.fail(HttpError(code.code, response.body.merge))
          }
        )
        .flatMap(decodeJson[T])

    override def retrievePage(pageId: UUID): IO[NotionError, Page] = decodeResponse[Page](notionClient.retrievePage(pageId))
    override def retrieveDatabase(databaseId: UUID): IO[NotionError, Database] =
      decodeResponse[Database](notionClient.retrieveDatabase(databaseId))
    override def retrieveUser(userId: UUID): IO[NotionError, User] = decodeResponse[User](notionClient.retrieveUser(userId))

    override def updatePage(patch: Page.Patch): IO[NotionError, Page] = decodeResponse[Page](notionClient.updatePage(patch))
  }
}
