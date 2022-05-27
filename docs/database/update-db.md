# Update a database

Notion allows you to update:
- The database title
- The properties schema of the database

For more information, you can check the [notion documentation](https://developers.notion.com/reference/update-a-database).

Strictly speaking, you have to provide a patch that contains the list of changes from the current page to the expected
one.

A patch is described by the following data structure:

```scala
final case class Patch(
      database:   Database,
      title:      Option[Seq[RichTextData]],
      properties: Map[String, Option[PatchPlan]]
)
```

PatchPlan describe the way to patch a property schema.

Using patch plan, you can:
- Update the name of the property schema
- Update the type of the property schema (cast the property from one type to another)

We provide several helper functions to update a patch with ease.

Here is an example:

```scala
import zio._
import zio.notion._
import zio.notion.dsl._
import zio.notion.model.database.Database

object UpdateDatabase extends ZIOAppDefault {
  def example: ZIO[Notion, NotionError, Unit] =
    for {
      database <- Notion.retrieveDatabase("6A074793-D735-4BF6-9159-24351D239BBC") // Insert your own database ID
      patch =
        database.patch
          .updateProperty($$"col1".patch.rename("Column 1"))
          .updateProperty($$"col2".patch.as(euro))
          .rename("My database")
      _ <- Notion.updateDatabase(patch)
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example.provide(Notion.layerWith("6A074793-D735-4BF6-9159-24351D239BBC")) // Insert your own bearer
}
```

`$$"col1"` is an alias for `$"col1".definition`.

In this example, we apply three different patches to the notion database:
- We rename the property schema "col1" to "Column 1"
- We cast the property "col2" to be a number with the **euro** unit
- We rename the database as "My database"
