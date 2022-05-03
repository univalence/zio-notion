package zio.notion

import io.circe.Decoder
import io.circe.parser.decode

import zio._
import zio.notion.NotionClient.NotionResponse
import zio.notion.model.Page

sealed trait Notion {
  protected def decodeJson[T: Decoder](content: String): IO[NotionError, T] =
    decode[T](content) match {
      case Right(t)    => ZIO.succeed(t)
      case Left(error) => ZIO.fail(JsonError(error))
    }

  def retrievePage(pageId: String): IO[NotionError, Page]

  def updatePage(patch: Page.Patch): IO[NotionError, Unit]
}

object Notion extends Accessible[Notion] {
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

    override def retrievePage(pageId: String): IO[NotionError, Page] = decodeResponse[Page](notionClient.retrievePage(pageId))

    override def updatePage(patch: Page.Patch): IO[NotionError, Unit] = notionClient.updatePage(patch).unit
  }
}
