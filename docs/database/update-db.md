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

We provide several helper function to update a patch with ease.

Here is an example:

```scala
import zio._
import zio.notion._
import zio.notion.dsl._
import zio.notion.model.page.Page

object UpdatePage extends ZIOAppDefault {
  def buildPatch(page: Page): Either[NotionError, Page.Patch] = {
    val date = LocalDate.of(2022, 2, 2)

    for {
      patch0 <- Right(page.patch)
      patch1 <- patch0.updateProperty($"col1".asNumber.patch.ceil)
      patch2 <- patch1.updateProperty($"col2".asDate.patch.between(date, date.plusDays(14)))
    } yield patch2.archive
  }

  def example: ZIO[Notion, NotionError, Unit] =
    for {
      page  <- Notion.retrievePage("6A074793-D735-4BF6-9159-24351D239BBC") // Insert your own page ID
      patch <- ZIO.fromEither(buildPatch(page))
      _     <- Notion.updatePage(patch)
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example.provide(Notion.layerWith("6A074793-D735-4BF6-9159-24351D239BBC")) // Insert your own bearer
}
```

In this example, we apply three different patches to the notion page:
- We update the property "col1" applying the *ceil* function to the already existing number
- We set the property "col2" with a start date and an end date 14 days later
- We archive the page
