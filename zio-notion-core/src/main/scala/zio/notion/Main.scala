package zio.notion

import sttp.client3.asynchttpclient.zio.{AsyncHttpClientZioBackend, SttpClient}

import zio._
import zio.notion.model.page.patch.PatchedProperty.PatchedNumber

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
  val configuration: NotionConfiguration      = NotionConfiguration(bearer = "secret_tx3gYOBSeFlYJOV0QtQKmAfoYzXQ9XTAuPZRt9XGwYF")

  def app: ZIO[Notion, NotionError, Unit] =
    for {
      page  <- Notion(_.retrievePage("28e158d738e54e2287c795525f650116"))
      _     <- Console.printLine(page.url).orDie
      patch <- ZIO.fromEither(page.patch.removeIcon.updateProperty(PatchedNumber.ceil.on("Number")))
      _     <- Notion(_.updatePage(patch))
    } yield ()

  override def run: ZIO[ZIOAppArgs, Any, Any] =
    app.tapError(e => Console.printLine(e.humanize)).provide(sttpLayer, ZLayer.succeed(configuration), NotionClient.live, Notion.live)
}
