package example

import zio._
import zio.notion._
import zio.notion.dsl._

import java.time.LocalDate

object QueryDatabase extends ZIOAppDefault {

  val notionConfiguration: NotionConfiguration =
    NotionConfiguration(
      bearer = "6A074793-D735-4BF6-9159-24351D239BBC" // Insert your own bearer
    )

  def example: ZIO[Notion, NotionError, Unit] = {
    val filter = $"col1".asNumber >= 10 and $"col2".asDate <= LocalDate.of(2022, 2, 2)
    val sorts  = $"col1".descending andThen byCreatedTime

    for {
      database <- Notion.queryDatabase("6A074793-D735-4BF6-9159-24351D239BBC", filter, sorts) // Insert your own page ID
      _ <-
        database.results.headOption match {
          case Some(page) => Console.printLine(s"The first page is ${page.id}").orDie
          case None       => Console.printLine("There is no page corresponding to the query").orDie
        }
    } yield ()
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example.provide(Notion.layerWith("6A074793-D735-4BF6-9159-24351D239BBC")) // Insert your own bearer
}
