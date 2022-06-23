# How to query a database

The `Notion.queryDatabase` function retrieve the metadata of a Notion database.

## Filter & Sorts

You can query the whole database, but you generally want to retrieve a subset of the database.

You can select what kind of information you want specifying a Query object containing information about how you want
to sort the result (based on certain column) or what do you want to retrieve (based on certain columns).

It can be a bit cumbersome to write the object by hand, we advise you to use the DSL to create our own filters and sorts.

To start using the DSL you need the following import:

```scala
import zio.notion.dsl._
```

### Column

Both filter and sorts can be applied to databases columns (ie: what's underneath a database property).

You can declare a column using `$` as such:

```scala
val col: Column = $"My property"
```
### Sorts

Sorts can be defined using a column:

```scala
val sorts = $"Col1"
```

It  means that we want the pages sorted using the **Col1** property in ascending order.

You can also use `col.descending` to sort them in descending order:

```scala
val sorts = $"Col1".descending
```

Sorts can be chained using the `andThen` keyword:

```scala
val sorts = $"Col1" andThen byCreatedTime.descending
```

### Filters

Filters can also be defined from a column.

Filters are chained using the `or` and `and` keywords:

```scala
val filter = $"Col1".asNumber >= 10 and $"Col2".asDate <= LocalDate.of(2022, 2, 2)
```

In order to apply a filter to a column you must first specify its property type as such:
- `$"my prop".asNumber`
- `$"myprop".asTitle`
- `$"myprop".asRichText`
- `$"myprop".asCheckbox`
- `$"myprop".asSelect`
- `$"myprop".asMultiSelect`
- `$"myprop".asDate`
- `$"myprop".asPeople`
- `$"myprop".asFiles`
- `$"myprop".asUrl`
- `$"myprop".asEmail`
- `$"myprop".asPhoneNumber`
- `$"myprop".asRelation`
- `$"myprop".asCreatedBy`
- `$"myprop".asLastEditedBy`
- `$"myprop".asCreatedTime`
- `$"myprop".asLastEditedTime`

All filter conditions can be found [here](https://developers.notion.com/reference/post-database-query-filter) or using
autocompletion tools.
