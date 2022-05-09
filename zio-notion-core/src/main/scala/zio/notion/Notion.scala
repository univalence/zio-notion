package zio.notion

import io.circe.Decoder
import io.circe.parser.decode

import zio._
import zio.notion.NotionClient.NotionResponse
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
}

object Notion {

  def apply[R1 <: Notion, E, A](f: Notion => ZIO[R1, E, A])(implicit tag: Tag[Notion], trace: Trace): ZIO[R1, E, A] = ZIO.serviceWithZIO[Notion](f)

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

    override def retrievePage(pageId: String): IO[NotionError, Page]             = decodeResponse[Page](notionClient.retrievePage(pageId))
    override def retrieveDatabase(databaseId: String): IO[NotionError, Database] = decodeResponse[Database](notionClient.retrieveDatabase(databaseId))
    override def retrieveUser(userId: String): IO[NotionError, User]             = decodeResponse[User](notionClient.retrieveUser(userId))
  }
}
