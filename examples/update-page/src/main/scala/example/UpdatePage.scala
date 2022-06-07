package example

import zio._
import zio.notion._
import zio.notion.dsl._

import java.time.LocalDate

object UpdatePage extends ZIOAppDefault {

  val notionConfiguration: NotionConfiguration =
    NotionConfiguration(
      bearer = "6A074793-D735-4BF6-9159-24351D239BBC" // Insert your own bearer
    )

  def example: ZIO[Notion, NotionError, Unit] = {
    val date       = LocalDate.of(2022, 2, 2)
    val operations = $"col1".asNumber.patch.ceil ++ $"col2".asDate.patch.between(date, date.plusDays(14)) ++ archive

    for {
      page <- Notion.retrievePage("6A074793-D735-4BF6-9159-24351D239BBC") // Insert your own page ID
      _    <- Notion.updatePage(page, operations)
    } yield ()
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example.provide(Notion.layerWith("6A074793-D735-4BF6-9159-24351D239BBC")) // Insert your own bearer
}
