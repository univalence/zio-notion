package zio.notion

import sttp.client3.asynchttpclient.zio.{AsyncHttpClientZioBackend, SttpClient}

import zio._
import zio.notion.dsl._
import zio.notion.model.database.Database
import zio.notion.model.database.query.{Filter, Sorts}

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
  def buildPatch(database: Database): Either[NotionError, Database.Patch] =
    for {
      patch0 <- Right(database.patch)
      patch1 <- patch0.updateProperty($$"col1".patch.rename("Column 1"))
      patch2 <- patch1.updateProperty($$"col2".patch.rename("Column 1"))

    } yield patch2.archive

  def example: ZIO[Notion, NotionError, Unit] =
    for {
      database <- Notion.retrieveDatabase("6A074793-D735-4BF6-9159-24351D239BBC") // Insert your own database ID
      patch =
        database.patch
          .updateProperty($$"col1".patch.rename("Column 1"))
          .updateProperty($$"col2".patch.as(euro))
          .rename("My database")
      _ <- Notion.updateDatabase(patch)
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example.provide(Notion.layerWith("6A074793-D735-4BF6-9159-24351D239BBC")) // Insert your own bearer
}
