# `Property`
## Overview
The `Property` type represents a notion page property

## Supported Types
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


## References
### `ToPatchedProperty`
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
