package zio.notion

import io.circe.syntax.EncoderOps
import sttp.client3._
import sttp.client3.asynchttpclient.zio.SttpClient
import sttp.model.Uri

import zio._
import zio.notion.NotionClient.NotionResponse
import zio.notion.model.page.Page
import zio.notion.model.printer

import java.util.UUID

trait NotionClient {
  def retrievePage(pageId: UUID): IO[NotionError, NotionResponse]
  def retrieveDatabase(databaseId: UUID): IO[NotionError, NotionResponse]
  def retrieveUser(userId: UUID): IO[NotionError, NotionResponse]

  def updatePage(patch: Page.Patch): IO[NotionError, NotionResponse]
}

object NotionClient {
  def retrievePage(pageId: UUID): ZIO[NotionClient, NotionError, NotionResponse] = ZIO.service[NotionClient].flatMap(_.retrievePage(pageId))
  def retrieveDatabase(databaseId: UUID): ZIO[NotionClient, NotionError, NotionResponse] =
    ZIO.service[NotionClient].flatMap(_.retrieveDatabase(databaseId))
  def retrieveUser(userId: UUID): ZIO[NotionClient, NotionError, NotionResponse] = ZIO.service[NotionClient].flatMap(_.retrieveUser(userId))

  def updatePage(patch: Page.Patch): ZIO[NotionClient, NotionError, NotionResponse] = ZIO.service[NotionClient].flatMap(_.updatePage(patch))

  type NotionResponse = Response[Either[String, String]]

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
      def handle: IO[NotionError, NotionResponse] = handleRequest(sttpClient.send(request))
    }

    private def handleRequest(request: Task[Response[Either[String, String]]]): IO[NotionError, NotionResponse] =
      request.mapError(t => ConnectionError(t))

    private def defaultRequest: RequestT[Empty, Either[String, String], Any] =
      basicRequest.auth
        .bearer(config.bearer)
        .header("Notion-Version", "2022-02-22")
        .header("Content-Type", "application/json")

    override def retrievePage(pageId: UUID): IO[NotionError, NotionResponse] =
      defaultRequest
        .get(uri"$endpoint/pages/$pageId")
        .handle

    override def retrieveDatabase(databaseId: UUID): IO[NotionError, NotionResponse] =
      defaultRequest
        .get(uri"$endpoint/databases/$databaseId")
        .handle

    override def retrieveUser(userId: UUID): IO[NotionError, NotionResponse] =
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
