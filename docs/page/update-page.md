# Update a page

Notion allows you to update:
- The properties content of the page (including its title)
- The icon of the page
- The cover of the page
- Whether you want to archive the page or not

For more information, you can check the [notion documentation](https://developers.notion.com/reference/patch-page).

Strictly speaking, you have to provide a patch that contains the list of changes from the current page to the expected 
one.

A patch is described by the following data structure: 

```scala
final case class Patch(
  page:       Page,
  properties: Map[String, Option[PatchedProperty]],
  archived:   Option[Boolean],
  icon:       Removable[Icon],
  cover:      Removable[Cover]
)
```

There is two foreign concepts here, **PatchedProperty** and **Removable** traits:
- A **Removable** is like an Option with one more possibility, indeed a **Removable[Icon]** means that we can set a new
  icon, we can remove the current one, or we can just ignore this property. A property is ignored by default.
- A **PatchedProperty** is the new value to apply to a specific property. There is a PatchedProperty for every 
  properties of a page. For the moment, you can't set a patched property if the property doesn't exist.

We provide several helper functions to update a patch with ease.

Here is an example:

```scala
import zio._
import zio.notion._
import zio.notion.dsl._
import zio.notion.model.page.Page

import java.time.LocalDate

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