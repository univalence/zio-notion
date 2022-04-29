package zio.notion

import sttp.client3._
import sttp.client3.asynchttpclient.zio.{AsyncHttpClientZioBackend, SttpClient}
import sttp.model.Uri

import zio._


trait NotionDatabaseFilter

trait NotionDatabaseSort

sealed trait Notion {
  type Page
  type DatabaseId = String

  // [Page: NotionDecoder : PageId]
  def database(
      databaseId: DatabaseId,
      filter: Option[NotionDatabaseFilter],
      sort: Option[NotionDatabaseSort]
  ): Task[String]

  def retrievePageBlocks(pageId: String): Task[String]
}

final case class Configuration(notion: NotionConfiguration)

final case class NotionConfiguration(bearer: String)

//TODO

// Créer le "NotionConnector" (name, JValue) => IO[HttpError, JValue]

// Créer l'ADT notion
// https://github.com/makenotion/notion-sdk-js/blob/main/src/api-endpoints.ts#L671
// RichTextElement
// RichText(seq[RichTextElement])
// Block

// Créer le "NotionClient(notionConnector)" map "https://api.notion.com/v1"
// resources : https://github.com/makenotion/notion-sdk-js/blob/main/src/api-endpoints.ts

// NotionParser : JValue => Either[Error, NValue]
// NotionMapper[CC] {
//   def apply(npage:NPage):Either[Error, CC]
//   def deref(npage:NPage):ZIO[Client, Error, CC]
// }

//Data :  case class Task(@name("tâche") task:String, status:String, @children body:Seq[Block])
//Pizza : case class Task(status: String, @childen body: RIO[Client, Seq[Block]])
//Chat  : case class Task[F[_]](status : String, @childen body: F[Seq[Block]])

/* trait Ref[T] { def deref:ZIO[NotionClient, Error, T] } */

object Notion extends Accessible[Notion] {
  val live: URLayer[Configuration with SttpClient, LiveNotionAPI] =
    ZLayer {
      for {
        config     <- ZIO.service[Configuration]
        sttpClient <- ZIO.service[SttpClient]
      } yield LiveNotionAPI(config.notion, sttpClient)
    }
}

final case class LiveNotionAPI(config: NotionConfiguration, sttpClient: SttpClient) extends Notion {
  val endpoint: Uri = uri"https://api.notion.com/v1"

  private def defaultRequest =
    basicRequest.auth
      .bearer(config.bearer)
      .header("Notion-Version", "2022-02-22")
      .header("Content-Type", "application/json")

  // https://developers.notion.com/reference/retrieve-a-page
  override def retrievePageBlocks(pageId: String): Task[String] = {
    val request: Request[Either[String, String], Any] =
      defaultRequest
        .get(uri"$endpoint/blocks/$pageId/children")

    sttpClient
      .send(request)
      .orDie
      .map(_.body)
      .absolve
      .mapError(e => new Exception(e))
      .resurrect
  }

  override def database(
      databaseId: DatabaseId,
      filter: Option[NotionDatabaseFilter],
      sort: Option[NotionDatabaseSort]
  ): Task[String] = {
    val request: Request[Either[String, String], Any] =
      defaultRequest
        .body("{}")
        .post(uri"$endpoint/databases/$databaseId/query")

    sttpClient
      .send(request)
      .orDie
      .map(_.body)
      .absolve
      .mapError(e => new Exception(e))
      .resurrect
  }
}

object Main extends ZIOAppDefault {

  val sttpLayer: Layer[Throwable, SttpClient] = AsyncHttpClientZioBackend.layer()
  val configuration: Configuration =
    Configuration(
      notion = NotionConfiguration(bearer = "secret_tx3gYOBSeFlYJOV0QtQKmAfoYzXQ9XTAuPZRt9XGwYF")
    )

  def app: ZIO[Notion, Nothing, Unit] =
    for {
      page     <- Notion(_.retrievePageBlocks("1c2d0a80-3321-4641-9615-f345185de05a")).orDie
      database <- Notion(_.database("3868f708ae46461fbfcf72d34c9536f9", None, None)).orDie
      _        <- Console.printLine(page).orDie
      _        <- Console.printLine(database).orDie

    } yield ()

  override def run: ZIO[ZIOAppArgs, Any, Any] = app.provide(sttpLayer, ZLayer.succeed(configuration), Notion.live)
}
