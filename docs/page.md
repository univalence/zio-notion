---
slug: /page
sidebar_position: 3
---

# Page

## Overview

You will deal with `Page` when handling database query results, retrieving a page or as a result of a page you just
created or updated.

A `Page` contains the metadata of a Notion page. We tend to follow the Notion api, and they don't provide the content
of the page. Instead, you have to deal with block instead.

For more information, you can check the [Notion documentation](https://developers.notion.com/reference/page).

## Retrieve a page

The easiest way to retrieve a page is:

```scala
val page: ZIO[Notion, NotionError, Page] = Notion.retrievePage("page-id")
```

No exhaustively, the page will contain its title, its properties and when it is created.

For more information, you can check the [notion documentation](https://developers.notion.com/reference/retrieve-a-page).

## Deal with properties

It can be troublesome to deal with page's properties. Indeed, the properties is a map composed by properties that can
theoretically be of any kind.

As an example, if you want to retrieve a number you will first have to :
- ensure that the property exist in the database
- ensure that the property is indeed a number property
- ensure that the property is fulfilled with data

You will easily have to write something like this:

```scala
val maybeProperty: Option[Property] = page.properties.get("number")

maybeProperty.map {
  case Some(property) =>
    property match {
      case number: Property.Number => number.number match {
        case Some(value) => // 🎉🎉🎉
        case None => // the property "name" exists, it is a number but the row has no data in it
      }
      case _ => // the property "name" exists but it is not a number
    }
  case None => // the property "name" does not exist
} 
```

That's why you can use the page function `propertiesAs[A]` to convert your properties into a defined case class.
Under the hood, it uses Magnolia to automatically derive the case class.

If we take the same example, you can now write something like this:

```scala
case class Properties(number: Double)

val propertiesOrError = page.propertiesAs[Properties]

propertiesOrError.map(_.number)
```

It will return a `Validation[ParsingError, A]`, this data structure is provided by
[zio-prelude](https://zio.github.io/zio-prelude/docs/functionaldatatypes/validation).

This way of dealing with properties has several interesting features:
- You can deal with optional value
- You can deal with list of values (Multiselect, People, etc.)
- You can create encoder for your own enumeration (Select)

### Using your own non-primitive types

If you need your case class to use non-primitive types you can add an implicit `PropertyConverter` such as the
following:

```scala
import zio.notion.Converter.required

final case class Properties(url: Url)
final case class Url(uri: String)

implicit val urlPropertyConverter: PropertyConverter[Url] = {
case Property.Files(_, files) =>
  required(files.headOption).flatMap {
    case Link.File(_, file) => Validation.succeed(Url(file.url))
    case Link.External(_, external) => Validation.succeed(Url(external.url))
  }
case _  => Validation.fail(NotParsableError("Url"))
}

val propertiesOrError = page.propertiesAs[Properties]
```

### Using a different name from Notion

The automatic derivation works by using the case class parameter name as the Notion property name. However, sometimes
it is not appropriate.

Imagine the following table:

| Name       | Price (in $) | Date of the shipping |
|------------|--------------|----------------------|
| purchase 1 | 1$           | 02/02/2022           |
| purchase 2 | 40$          | 01/02/2022           |
| purchase 3 | 20$          | 03/02/2022           |

For each page, if you want to retrieve the properties using automatic derivation, you will have to write something 
like :

```scala
import java.time.LocalDate

final case class Properties(
  Name: String,
  `Price (in $)`: Double,
  `Date of the shipping`: LocalDate
)
```

Let's be honest, it is not practical. You still can enforce camelCase convention to your Notion users but is not
convenient too since Notion is meant to be human-readable.

Luckily, you can annotate your parameters :

```scala
import java.time.LocalDate

final case class Properties(
  @NotionColumn("Name") name: String,
  @NotionColumn("Price (in $)") price: Double,
  @NotionColumn("Date of the shipping") shippingDate: LocalDate
)
```

## Update a page

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
def updatePage(pageId: String, operation: Operation.Stateless): IO[NotionError, Page]
def updatePage(page: Page, operation: Operation): IO[NotionError, Page]
```

We provide several kind of operations that can compose multiple operations:

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
val operation = removeProperty("name")         // Remove the property "name" of the page (Stateless)
val operation = $"col1".asCheckbox.patch.check // Check the col1 checkbox property (Stateless)
val operation = $"col1".asNumber.patch.ceil    // Apply a transformation to the col1 number property (Stateful)
```

We advise you to check autocompletion for `$"col1".as`, we provide operations for all notion types.

You also can create your own, as an example, if I want to multiply a number by itself:

```scala
val operation = $"col1".asNumber.patch.update(n => n * n) // It is an update (Stateful)
```

## Create a page

You can also create a new page by providing the parent page in both a page and a database.