
# Patch & Operation
## Overview
`Patch` is an inner class of `Database` that allow you to keep the state of a page's update

`Operation` is an inner class of `Patch` that allow you to build descriptions of a database's update.
## Stateless vs Stateful `Operations`

Stateless operations are operations that do not need values from the targeted database

| Operation    | Parameters                                        | Type      | Notes                     |
|--------------|---------------------------------------------------|-----------|---------------------------|
| RemoveColumn | name: String                                      | Stateless | Removes a database column |
| SetTitle     | title: Seq[RichTextFragment]                      | Stateless | Sets database title       |
| CreateColumn | name: String, schema: PropertySchema              | Stateless | Creates a database column |
| UpdateTitle  | f: Seq[RichTextFragment] => Seq[RichTextFragment] | Stateful  | Update a database title   |
| UpdateColumn | name: String, update: PatchedPropertyDefinition   | Stateful  | Update a column           |

## Example

Example of an operation description
- It ceil the number property "col1" of a page
- It turns the date property "col2" of a page into a range frome `date` and `date plus 14 days`
- It archives (deletes) the page

```scala
val operations = $"col1".asNumber.patch.ceil ++ $"col2".asDate.patch.between(date, date.plusDays(14)) ++ archive
```

## Referenced methods
todo links