# How to query a database

Query a database is useful if you want to retrieve all the pages composing the database. 

## Pagination

If you call `Notion.queryDatabase` to retrieve your database content, you will need to give a pagination object too.
The pagination object will tell Notion what to retrieve. Using this object you can specify how many pages you want to
retrieve and when to start the count. We advise looking at
[Notion pagination documentation](https://developers.notion.com/reference/pagination) for more information.

Nevertheless, we provide the function `Notion.queryAllDatabase` that will handle pagination for you.

## Filter & Sorts

You can query the whole database, but you generally want to retrieve a subset of the database.

You can select what kind of information you want specifying a Query object containing information about how you want
to sort the result (based on certain column) or what do you want to retrieve (based on certain column).

It can be a bit cumbersome to write the object by hand, we advise you to use the DSL to create our own filters and sorts.

To start using the DSL you need the following import:

```scala
import zio.notion.dsl._
```

### Column

Both filter and sorts can be applied to databases columns (ie: what's underneath a database property).

You can declare a column using `$` as such:

```scala
val col: Column = $"My property"
```
### Sorts

Sorts can be defined using a column:

```scala
val sorts = $"Col1"
```

It  means that we want the pages sorted using the **Col1** property in ascending order.

You can also use `col.descending` to sort them in descending order:

```scala
val sorts = $"Col1".descending
```

Sorts can be chained using the `andThen` keyword:

```scala
val sorts = $"Col1" andThen byCreatedTime.descending
```

### Filters

Filters can also be defined from a column.

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

All filter conditions can be found [here](https://developers.notion.com/reference/post-database-query-filter) or using
autocompletion tools.

## Example

```scala
import zio._
import zio.notion._
import zio.notion.dsl._

import java.time.LocalDate

object QueryDatabase extends ZIOAppDefault {
  def example: ZIO[Notion, NotionError, Unit] = {
    val filter = $"col1".asNumber >= 10 and $"col2".asDate <= LocalDate.of(2022, 2, 2)
    val sorts  = $"col1".descending andThen byCreatedTime

    for {
      database <- Notion.queryAllDatabase("6A074793-D735-4BF6-9159-24351D239BBC", filter combine sorts) // Insert your own database ID
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
```