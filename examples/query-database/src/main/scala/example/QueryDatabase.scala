package example

import zio._
import zio.notion._
import zio.notion.dsl._
import zio.notion.model.database.query.{Filter, Sorts}

import java.time.LocalDate

object QueryDatabase extends ZIOAppDefault {

  def example: ZIO[Notion, NotionError, Unit] = {
    val filter: Filter = $"col1".asNumber >= 10 and $"col2".asDate <= LocalDate.of(2022, 2, 2)
    val sorts: Sorts   = $"col1".descending andThen byCreatedTime

    for {
      database <- Notion.queryAllDatabase("6A074793-D735-4BF6-9159-24351D239BBC", filter combine sorts)
      pages = database.results
      _ <-
        pages.headOption match {
          case Some(page) => Console.printLine(s"The first page is ${page.id}").orDie
          case None       => Console.printLine("There is no page corresponding to the query").orDie
        }
    } yield ()
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example.provide(Notion.layerWith("6A074793-D735-4BF6-9159-24351D239BBC")) // Insert your own bearer
}
