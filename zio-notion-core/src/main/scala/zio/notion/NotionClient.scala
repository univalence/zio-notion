package zio.notion

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import sttp.client3._
import sttp.client3.asynchttpclient.zio.SttpClient
import sttp.model.Uri

import zio._
import zio.notion.NotionClient.NotionResponse
import zio.notion.NotionError._
import zio.notion.model.page.Page
import zio.notion.model.printer

trait NotionClient {
  def retrievePage(pageId: String): IO[NotionError, NotionResponse]
  def retrieveDatabase(databaseId: String): IO[NotionError, NotionResponse]
  def retrieveUser(userId: String): IO[NotionError, NotionResponse]

  def updatePage(patch: Page.Patch): IO[NotionError, NotionResponse]
}

object NotionClient {
  def retrievePage(pageId: String): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.service[NotionClient].flatMap(_.retrievePage(pageId))
  def retrieveDatabase(databaseId: String): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.service[NotionClient].flatMap(_.retrieveDatabase(databaseId))
  def retrieveUser(userId: String): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.service[NotionClient].flatMap(_.retrieveUser(userId))

  def updatePage(patch: Page.Patch): ZIO[NotionClient, NotionError, NotionResponse] = ZIO.service[NotionClient].flatMap(_.updatePage(patch))

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

    override def updatePage(patch: Page.Patch): IO[NotionError, NotionResponse] =
      defaultRequest
        .patch(uri"$endpoint/pages/${patch.page.id}")
        .body(printer.print(patch.asJson))
        .handle
  }
}
