---
slug: /pageTest
sidebar_position: 2
---
# `Page`
## Overview
`Page` is the type that represents a Notion page.

You will need this type when handling database query results, retrieving a page or as a result of a page you just created or updated.

## `Page` parameters
    
| Parameter       | Type                  | Notes                                                                |
|-----------------|-----------------------|----------------------------------------------------------------------|
| createdTime     | OffsetDateTime        | Time at which the page was created                                   |
| lastEditedTime  | OffsetDateTime        | Time at which the page was last edited                               |
| createdBy       | Id                    | Returns the author's id                                              |
| lastEditedBy    | Id                    | Returns the ast editor's id                                          |
| id              | String                | UUID of the page                                                     |
| cover           | Option[Cover]         | URI of the page's cover picture file                                 |
| icon            | Option[Icon]          | Emoji or URI of the page's icon picture file                         |
| parent          | Parent                | Parent page or database. Can be used like so: "xxx".asParentDatabase |
| archived        | Boolean               | State of the page archiving                                          |
| properties      | Map[String, Property] | Property map. Is described below in the documentation                |
| url             | String                | Page's URL                                                           |

## `Property`
### Overview
The `Property` type represents a notion page property

### Supported Types
| Property       | Parameters                                  |
|----------------|---------------------------------------------|
| Number         | id: String, number: Option[Double]          | 
| Url            | id: String, url: Option[String]             | 
| Select         | id: String, select: Option[SelectData]      | 
| MultiSelect    | id: String, multiSelect: List[SelectData]   | 
| Date           | id: String, date: Option[Period]            | 
| DateTime       | id: String, date: Option[TimePeriod]        | 
| Email          | id: String, email: Option[String]           | 
| PhoneNumber    | id: String, phoneNumber: Option[String]     | 
| Checkbox       | id: String, checkbox: Option[Boolean]       | 
| Files          | id: String, files: Seq[Link]                | 
| Title          | id: String, title: Seq[RichTextFragment]    | 
| RichText       | id: String, richText: Seq[RichTextFragment] | 
| People         | id: String, people: Seq[User]               | 
| CreatedBy      | id: String, createdBy: Id                   | 
| CreatedTime    | id: String, createdTime: String             | 
| LastEditedBy   | id: String, lastEditedBy: Id                | 
| LastEditedTime | id: String, lastEditedTime: String          | 
| Formula        | id: String, formula: FormulaData            | 
| Rollup         | id: String, rollup: RollupData              | 
| Relation       | id: String, relation: Seq[Id]               | 


### References
#### `ToPatchedProperty`
`ToPatchedProperty` allows you to convert a `Property` into a `PatchedProperty`

| Method | Parameters         |
|--------|--------------------|
| apply  | property: Property |

This mechanism can be pretty useful when you want to create a new `Page` from another `Page`

```scala
val oldTaskProperties =
  page.properties.view
    .mapValues(ToPatchedProperty.apply)
    .collect { case (key, Some(value)) => key -> value }
    .toMap

val newTaskProperties =
  oldTaskProperties ++ Map(
    "NewSelect" -> PatchedProperty.PatchedSelect(None, Some("Todo")),
    "Due"       -> PatchedProperty.PatchedDate(nextDueDate, None)
  )
```

## `Patch` & `Operation`
### Overview
`Patch` is an inner class of `Page` that allow you to keep the state of a page's update

`Operation` is an inner class of `Patch` that allow you to build descriptions of a page's update.
### Stateless vs Stateful `Operations`

| Operation      | Parameters                                        | Type      | Notes |
|----------------|---------------------------------------------------|-----------|-------|
| Archive        |                                                   | Stateless | todo  |
| Unarchive      |                                                   | Stateless | todo  |
| RemoveIcon     |                                                   | Stateless | todo  |
| RemoveCover    |                                                   | Stateless | todo  |
| SetIcon        | icon: Icon                                        | Stateless | todo  |
| SetCover       | cover: Cover                                      | Stateless | todo  |
| RemoveProperty | name: String                                      | Stateless | todo  |
| SetProperty    | name: String, value: PatchedProperty              | Stateless | todo  |
| UpdateProperty | name: String, transform: UpdateProperty.Transform | Stateful  | todo  |

### Referenced methods  
#### `updatePage`
`updatePage` allows you to update a Notion page

todo param table

```scala
todo example
```
## `PatchedProperty`
`PatchedProperty` are properties for pages that you intend to create or update

### Supported types
| Property           | Parameters                                                                   | Notes |
|--------------------|------------------------------------------------------------------------------|-------|
| PatchedTitle       | title: Seq[RichTextFragment]                                                 | todo  |
| PatchedRichText    | richText: Seq[RichTextFragment]                                              | todo  |
| PatchedNumber      | number: Double                                                               | todo  |
| PatchedCheckbox    | checkbox: Boolean                                                            | todo  |
| PatchedSelect      | id: Option[String], name: Option[String]                                     | todo  |
| PatchedMultiSelect | multiSelect: List[PatchedSelect]                                             | todo  |
| PatchedDate        | start: LocalDate, end: Option[LocalDate]                                     | todo  |
| PatchedDateTime    | start: OffsetDateTime, end: Option[OffsetDateTime], timeZone: Option[String] | todo  |
| PatchedPeople      | people: Seq[User]                                                            | todo  |
| PatchedRelation    | relation: Seq[Id]                                                            | todo  |
| PatchedFiles       | files: Seq[Link]                                                             | todo  |
| PatchedUrl         | url: String                                                                  | todo  |
| PatchedEmail       | email: String                                                                | todo  |
| PatchedPhoneNumber | phoneNumber: String                                                          | todo  |


### Referenced methods
#### createPageInDatabase
todo example
todo param table
#### createPageInPage
todo example
todo param table
#### updatePage
todo example
todo param table
## Referenced methods
### propertiesAs[A]
`propertiesAs[A]` allows you to destructure a `Page` into a `case class` `A`


It can be troublesome to deal with page's properties. Indeed, the properties is a map composed by properties that can
theoretically be of any kind.

As an example, if you want to retrieve a number you will first have to :
- ensure that the property exist in the database
- ensure that the property is indeed a number property
- ensure that the property is fulfilled with data

You will easily have to write something like this:

```scala
val maybeProperty: Option[Property] = page.properties.get("name")

maybeProperty.collect{
  case number: Property.Number => number.number match {
    case Some(value) => // we can finally do something with the value
    case None => // the value exists in the database but the row has no data in it
  }
}
```

That's why you can use the page function `propertiesAs[A]` to convert your properties into a defined case class.
Under the hood, it uses Magnolia to automatically derive the case class.

If we take the same example, you can now write something like this:

```scala
case class PropertiesRepresentation(number: Double)

val propertiesOrError = page.propertiesAs[PropertiesRepresentation]

propertiesOrError.map(_.number)
```

If you need your case class to use non-primitive types you can add an implicit `PropertyConverter` such as the following: 

```scala
final case class PropertiesRepresentation(other: YetAnotherCC)
final case class YetAnotherCC(uri: String, uriToBytes: Array[Byte])

object PropertiesRepresentation {
  implicit val otherCc: PropertyConverter[YetAnotherCC] = {
    case Property.Files(_, files) =>
      Validation.succeed(
        files.collect {
          case Link.External(name, external) => YetAnotherCC(external.url, external.url.getBytes )
          case Link.File(name, temp)         => YetAnotherCC(temp.url, temp.url.getBytes )
        }
      )
    case _ => Validation.succeed(Seq.empty)
  }
}

val propertiesOrError = page.propertiesAs[PropertiesRepresentation]
```

It will return a `Validation[ParsingError, A]`, this data structure is provided by
[zio-prelude](https://zio.github.io/zio-prelude/docs/functionaldatatypes/validation).

This way of dealing with properties has several interesting features:
- You can deal with optional value
- You can deal with list of values (Multiselect, People, etc.)
- You can create encoder for your own enumeration (Select)

I advise you to look at the example
[retrieve-page](https://github.com/univalence/zio-notion/tree/master/examples/retrieve-page) for more information.



### retrievePage
To retrieve a page you can call the following function providing the id of the page:

```scala
for {
  page <- Notion.retrievePage("page-id")
} yield page
```

This function does not retrieve the content of a page but only its metadata. We tend to follow the notion api, and they
don't provide this information when you retrieve a page. Instead, you have to call
[this endpoint](https://developers.notion.com/reference/get-block-children) which is not implement in ZIO Notion yet.

For more information, you can check the [notion documentation](https://developers.notion.com/reference/retrieve-a-database).

| Parameter | Type   | Notes |
|-----------|--------|-------|
| pageId    | String | todo  |

## Recap
## Full example