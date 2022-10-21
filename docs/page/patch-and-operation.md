
# Patch & Operation
## Overview
`Patch` is an inner class of `Page` that allow you to keep the state of a page's update

`Operation` is an inner class of `Patch` that allow you to build descriptions of a page's update.
## Stateless vs Stateful `Operations`

Stateless operations are operations that do not need values from the targeted page

| Operation      | Parameters                                        | Type      | Notes                                |
|----------------|---------------------------------------------------|-----------|--------------------------------------|
| Archive        |                                                   | Stateless | Deletes a page                       |
| Unarchive      |                                                   | Stateless | Brings a page back from trash        |
| RemoveIcon     |                                                   | Stateless | Removes page icon                    |
| RemoveCover    |                                                   | Stateless | Removes cover image                  |
| SetIcon        | icon: Icon                                        | Stateless | Sets page icon                       |
| SetCover       | cover: Cover                                      | Stateless | Sets page cover image                |
| RemoveProperty | name: String                                      | Stateless | Removes a page property value        |
| SetProperty    | name: String, value: PatchedProperty              | Stateless | Adds a value to a page property      |
| UpdateProperty | name: String, transform: UpdateProperty.Transform | Stateful  | Update a property given an older one |

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