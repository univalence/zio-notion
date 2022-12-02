# How to query a Database

If your intention is building bots to manage your content, chances are that you'll need to retrieve a bunch of pages 
from a database.

In this small example, we'll get you started on how to query a Notion database, filtering and sorting the page that
you want to retrieve.

To query a database you will need your database identifier. It can be retrieved directly from your browser url when
you navigate to your database. The url will be something like: 
https://www.notion.so/`organization-name`/`database-id`?v=`view-id`

## Example

Let's say you have this kind of DB: 

| Name       | Price | Date       |
|------------|-------|------------|
| purchase 1 | 1$    | 02/02/2022 |
| purchase 2 | 40$   | 01/02/2022 |
| purchase 3 | 20$   | 03/02/2022 |

The example below retrieve the most expensive product since 02/0/2/22. 

```scala
import zio._
import zio.notion._
import zio.notion.dsl._

object QueryDatabase extends ZIOAppDefault {

  def example: ZIO[Notion, NotionError, Unit] = {
    val filter: Filter = $"Date".asDate >= LocalDate.of(2022, 2, 2)
    val sorts: Sorts = $"Price".descending
    val pagination: Pagination = Pagination(1, None) // We only need to retrieve the first item.

    for {
      database <- Notion.queryDatabase("XXX-YYY-ZZZ", filter combine sorts, pagination)
      _ <- 
        database.results.headOption match {
          case Some(page) => Console.printLine(s"The first page is ${page.id}").orDie
          case None       => Console.printLine("There is no page corresponding to the query").orDie
        }
    } yield ()
  }

  override def run = example.provide(Notion.layerWith("secret_XyZ")) // Insert your own bearer
}
```