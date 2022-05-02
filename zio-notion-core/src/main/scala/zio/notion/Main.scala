package zio.notion

import sttp.client3.asynchttpclient.zio.{AsyncHttpClientZioBackend, SttpClient}

import zio._

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

object Main extends ZIOAppDefault {

  val sttpLayer: Layer[Throwable, SttpClient] = AsyncHttpClientZioBackend.layer()
  val configuration: NotionConfiguration =
    NotionConfiguration(bearer = "secret_tx3gYOBSeFlYJOV0QtQKmAfoYzXQ9XTAuPZRt9XGwYF")

  def app: ZIO[Notion, NotionError, Unit] =
    for {
      page <- Notion(_.retrievePage("1c2d0a80-3321-4641-9615-f345185de05a"))
      _    <- Console.printLine(page.url).orDie
    } yield ()

  override def run: ZIO[ZIOAppArgs, Any, Any] =
    app.provide(sttpLayer, ZLayer.succeed(configuration), NotionClient.live, Notion.live)
}
