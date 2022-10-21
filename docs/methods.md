---
slug: /methods
sidebar_position: 8
---
# Methods

This section covers every method definition with an example

## retrievePage
| Method           | Parameters       | Return type                 |
|------------------|------------------|-----------------------------|
| **retrievePage** | pageId: `String` | `IO`[`NotionError`, `Page`] |

To retrieve a page you can call the following function providing the id of the page:

```scala
for {
  page <- Notion.retrievePage("page-id")
} yield page
```

This function does not retrieve the content of a page but only its metadata. 

We tend to follow the Notion API, and they don't provide the content of a page when you "retrieve" it. 

Instead, you have to call [this endpoint](https://developers.notion.com/reference/get-block-children) which is not implement in ZIO Notion yet.

For more information, you can check the [notion documentation](https://developers.notion.com/reference/retrieve-a-database).


## retrieveDatabase
| Method                   | Parameters                                                                                                                                    | Return type                          |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| **retrieveDatabase**     | databaseId: `String`                                                                                                                          | `IO`[`NotionError`, `Database`]      |



## retrieveUser
| Method                   | Parameters                                                                                                                                    | Return type                          |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| **retrieveUser**         | userId: `String`                                                                                                                              | `IO`[`NotionError`, `User`]          |
## retrieveUsers
| Method                   | Parameters                                                                                                                                    | Return type                          |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| **retrieveUsers**        | pagination: `Pagination`                                                                                                                      | `IO`[`NotionError`, `Users`]         |
## retrieveBlock
| Method                   | Parameters                                                                                                                                    | Return type                          |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| **retrieveBlock**        | blockId: `String`                                                                                                                             | `IO`[`NotionError`, `Block`]         |
## retrieveBlocks
| Method                   | Parameters                                                                                                                                    | Return type                          |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| **retrieveBlocks**       | pageId: `String`, pagination: `Pagination`                                                                                                    | `IO`[`NotionError`, `Blocks`]        |
## queryDatabase
| Method                   | Parameters                                                                                                                                    | Return type                          |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| **queryDatabase**        | databaseId: `String`, query: `Query`, pagination: `Pagination`                                                                                | `IO`[`NotionError`, `DatabaseQuery`] |

queryDatabase is a method allowing you to get pages from a database given some `Sorts` and `Filters`

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

## updatePage
| Method         | Parameters                                                     | Return type                 |
|----------------|----------------------------------------------------------------|-----------------------------|
| **updatePage** | pageId: `String`, operations: `Page.Patch.StatelessOperations` | `IO`[`NotionError`, `Page`] |
| **updatePage** | page: `Page`, operations: `Page.Patch.Operations`              | `IO`[`NotionError`, `Page`] |

`updatePage` allows you to update a Notion page

```scala
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
```

## updateDatabase
| Method                   | Parameters                                                                                                                                    | Return type                          |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| **updateDatabase**       | databaseId: `String`, operations: `Database.Patch.StatelessOperations`                                                                        | `IO`[`NotionError`, `Database`]      |
## updateDatabase
| Method                   | Parameters                                                                                                                                    | Return type                          |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| **updateDatabase**       | database: `Database`, operations: `Database.Patch.Operations`                                                                                 | `IO`[`NotionError`, `Database`]      |
## createDatabase
| Method                   | Parameters                                                                                                                                    | Return type                          |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| **createDatabase**       | pageId: `String`, title: `Seq[RichTextFragment]`, icon: `Option[Icon]`, cover: `Option[Cover]`, properties: `Map[String, PropertySchema]`     | `IO`[`NotionError`, `Database`]      |

## createPageInPage
| Method                   | Parameters                                                                                                                                    | Return type                          |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| **createPageInPage**     | parent: `PageId`, title: `Option[PatchedProperty]`, icon: `Option[Icon]`, cover: `Option[Cover]`, children: `Seq[BlockContent]`               | `IO`[`NotionError`, `Page`]          |

createPageInPage` uses `PatchedTitle` in order to create a page

```scala
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val dbId = "xxx"
    val title: PatchedTitle = PatchedTitle(Seq(RichTextFragment.default("title")))
    val program: ZIO[Notion, NotionError, Unit] =
      for {
        page <- Notion.createPageInPage(dbId.asParentPage, Some(title), None, None, Seq.empty)
      } yield ()
    program
      .tapError(err => Console.printLine(err.humanize))
      .provide(Notion.layerWith("secret_xxx"))
  }
```

## createPageInDatabase
| Method                   | Parameters                                                                                                                                    | Return type                          |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| **createPageInDatabase** | parent: `DatabaseId`, properties: `Map[String, PatchedProperty]`, icon: `Option[Icon]`, cover: `Option[Cover]`, children: `Seq[BlockContent]` | `IO`[`NotionError`, `Page`]          |


`createPageInDatabase` uses `PatchedProperties` in order to create a page inside a database


```scala
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val dbId = "xxx"
    val props: Map[String, PatchedProperty] =
      Map(
        // Notion's rich-text can handle mentions annotated content and equations.
        // The `.default` allows you to simply write text
        "Name"   -> PatchedTitle(Seq(RichTextFragment.default("title"))),
        "IsTrue" -> PatchedCheckbox(true)
      )
    val program: ZIO[Notion, NotionError, Unit] =
      for {
        page <- Notion.createPageInDatabase(dbId.asParentDatabase, props, None, None, Seq.empty)
      } yield ()
    program
      .tapError(err => Console.printLine(err.humanize))
      .provide(Notion.layerWith("secret_xxx"))
  }
```







