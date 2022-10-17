# How to query a Database

If your intention is building bots to manage your content, chances are that you'll need to retrieve a bunch of pages from a database.

In this small example, we'll get you started on how to query a Notion database with `filters` and `sorts` 

## What you'll need
### Your database ID:
It can be found by hitting `⌘ + L` while on your database

It will look something like so: notion.so/univalence/`DB-ID`?v=`View-ID`
### To know what you'd like to filter on
Basically column names and types
### Your Bearer token 
Visit https://www.notion.so/my-integrations 

Bearer tokens start with `secret_`
## Related documentation
TODO: links

## Example

Let's say you have this kind of DB: 

| Name       | price | date       |
|------------|-------|------------|
| purchase 1 |    1$ | 02/02/2022 |
| purchase 2 |   10$ | 01/02/2022 |
| purchase 3 |   10$ | 02/02/2022 |


```scala
object QueryDatabase extends ZIOAppDefault {

  def example: ZIO[Notion, NotionError, Unit] = {
    val filter: Filter = $"price".asNumber >= 10 and $"date".asDate <= LocalDate.of(2022, 2, 2)
    val sorts: Sorts = $"price".descending andThen byCreatedTime

    for {
      database <- Notion.queryAllDatabase("XXX-YYY-ZZZ", filter combine sorts)
      pages = database.results
      _ <-
        pages.headOption match {
          case Some(page) => Console.printLine(s"The first page is ${page.id}").orDie
          case None       => Console.printLine("There is no page corresponding to the query").orDie
        }
    } yield ()
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example.provide(Notion.layerWith("secret_XyZ")) // Insert your own bearer
}
```