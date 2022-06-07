# Update a page

Notion allows you to update:
- The properties content of the page (including its title)
- The icon of the page
- The cover of the page
- Whether you want to archive the page or not

For more information, you can check the [notion documentation](https://developers.notion.com/reference/patch-page).

Strictly speaking, you have to provide a list of operations describing the list of changes from the current page to the 
expected one.

There is two types of operations :
- Stateless operations are operations that does not require the current state of the page to generate a patch. As an
  example, `removeIcon` is a stateless operation because we don't need to explicitly know if the page already has an
  icon or not.
- Stateful operations are operations that does require the current state of the page. The only current stateful 
  operation is `UpdateProperty`. It indeed requires the current page property to update it.

We explicitly differentiate the operations because stateless operations does not require a page to work. It means that
we don't have to retrieve the page first to update it. That is why the **Notion** interface provides several
update methods :

```scala
def updatePage(pageId: String, operations: StatelessOperations): IO[NotionError, Page]
def updatePage(page: Page, operations: Operations): IO[NotionError, Page]
def updatePage(pageId: String, operations: Operation.Stateless): IO[NotionError, Page]
def updatePage(page: Page, operations: Operation): IO[NotionError, Page]
```

We provide several kind of operations that can compose:

```scala
import zio.notion.dsl._ // We advise you to import the dsl

val operations = $"col1".asCheckbox.patch.check ++ removeIcon
```

Here is a non-exhaustive list of operation:

```scala
val operation = archive                        // Archive the page (Stateless)
val operation = unarchive                      // Unarchive the page (Stateless)
val operation = removeIcon                     // Remove the current icon of the page (Stateless)
val operation = removeCover                    // Remove the current cover of the page (Stateless)
val operation = setIcon(newIcon)               // Set a new icon to the page (Stateless)
val operation = setCover(newCover)             // Set a new cover to the page (Stateless)
val operation = removeProperty(propertyName)   // Remove the propertyName property of the page (Stateless)
val operation = $"col1".asCheckbox.patch.check // Check the col1 checkbox property (Stateless)
val operation = $"col1".asNumber.patch.ceil    // Apply a transformation to the col1 number property (Stateful)
```

We advise you to check autocompletion for `$"col1".as`, we provide operations for all notion types.

You also can create your own, as an example, if I want to multiply a number by itself:

```scala
val operation = $"col1".asNumber.patch.update(n => n * n) // It is an update (Stateful)
```
