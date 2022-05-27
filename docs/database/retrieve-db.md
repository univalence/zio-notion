# How to retrieve a DB
## Example
```scala
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import zio._
import zio.notion._

object QueryDatabase extends ZIOAppDefault {
  val notionConfiguration: NotionConfiguration =
    NotionConfiguration(
      bearer = "YOUR_BEARER"
    )

  def example: ZIO[Notion, NotionError, Unit] = {
    for {
      db <- Notion.retrieveDatabase("YOUR_DB_ID")
      _ <- Console.printLine(db.properties).orDie
    } yield ()
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example.provide(
      AsyncHttpClientZioBackend.layer(),
      notionConfiguration.asLayer,
      NotionClient.live,
      Notion.live
    )
}
```
## Return type
The `Database` type provides you with several properties and methods
### Properties
- createdTime
- lastEditedTime
- createdBy
- lastEditedBy
- id
- title
- cover
- icon
- parent
- archived
- properties
- url

### Methods

The `Database` type provides you with patching functions described in the patch a `How to patch a database` section 