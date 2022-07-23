package example

import java.time.LocalDate

object UpdateDatabase extends ZIOAppDefault {

  def example: ZIO[Notion, NotionError, Unit] = {
    val operations = $$"col1".patch.as(dollar)

    for {
      page <- Notion.retrieveDatabase("6A074793-D735-4BF6-9159-24351D239BBC") // Insert your own page ID
      _    <- Notion.updateDatabase(db, operations)
    } yield ()
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example.provide(Notion.layerWith("6A074793-D735-4BF6-9159-24351D239BBC")) // Insert your own bearer
}
