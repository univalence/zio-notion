# How to query a database
## What's needed
In order to query a Database(DB), you will need: 
- The DB's id
## Example
```scala
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend

import zio._
import zio.notion._
import zio.notion.dsl._
import zio.notion.dsl.DatabaseQueryDSL._

import java.time.LocalDate

object QueryDatabase extends ZIOAppDefault {
  val notionConfiguration: NotionConfiguration =
    NotionConfiguration(
      bearer = "6A074793-D735-4BF6-9159-24351D239BBC" // Insert your own bearer
    )

  def example: ZIO[Notion, NotionError, Unit] = {
    val filter = $"Col1".asNumber >= 10 and $"Col2".asDate <= LocalDate.of(2022, 2, 2)
    val sorts  = $"Col1".descending andThen createdTime

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
    example.provide(
      AsyncHttpClientZioBackend.layer(),
      notionConfiguration.asLayer,
      NotionClient.live,
      Notion.live
    )
}
```
## DSL operators
### Column
Both filters and sorts can be applied to databases columns (ie: what's underneath a db property).

You can declare a column using `$` as such: 
```scala
val col: Column = $"My property"
```
### Sorts
Sorts can be chained using the `andThen` keyword:

```scala
val sorts = $"Col1" andThen createdTime.descending
```

Columns or sorted in ascending mode by default

### Filters

Filters are chained using the `or` and `and` keywords:
```scala
val filter = $"Col1".asNumber >= 10 and $"Col2".asDate <= LocalDate.of(2022, 2, 2)
```

In order to apply a filter to a column you must first specify its property type as such: 
- `$"my prop".asNumber`
- `$"myprop".asTitle`
- `$"myprop".asRichText`
- `$"myprop".asCheckbox`
- `$"myprop".asSelect`
- `$"myprop".asMultiSelect`
- `$"myprop".asDate`
- `$"myprop".asPeople`
- `$"myprop".asFiles`
- `$"myprop".asUrl`
- `$"myprop".asEmail`
- `$"myprop".asPhoneNumber`
- `$"myprop".asRelation`
- `$"myprop".asCreatedBy`
- `$"myprop".asLastEditedBy`
- `$"myprop".asCreatedTime`
- `$"myprop".asLastEditedTime`

All filter conditions can be found [here](https://developers.notion.com/reference/post-database-query-filter) or using autocompletion tools



