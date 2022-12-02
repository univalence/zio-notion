---
slug: /database
sidebar_position: 2
---

# Database

## Overview

You will deal with `Database` when handling databases.

## Retrieve a database

The easiest way to retrieve a database is:

```scala
val database: ZIO[Notion, NotionError, Database] = Notion.retrieveDatabase("database-id")
```

No exhaustively, the database will contain its title, its column definitions and when it is created.

For more information, you can check the [Notion documentation](https://developers.notion.com/reference/retrieve-a-database).

## Query a database

You will need to query the database if you want to retrieve the pages that compose the database.

You can query the whole database, but you generally want to retrieve a subset of the database.

You can select what kind of information you want specifying a Query object containing information about how you want
to sort the result (based on certain columns) or what do you want to retrieve (based on certain columns).

It can be a bit cumbersome to write the object by hand, we advise you to use the DSL to create our own filters and 
sorts.

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
val sorts: Sorts = $"Col1"
```

It  means that we want the pages sorted using the **Col1** property in ascending order.

You can also use `col.descending` to sort them in descending order:

```scala
val sorts: Sorts = $"Col1".descending
```

Sorts can be chained using the `andThen` keyword:

```scala
val sorts: Sorts = $"Col1" andThen byCreatedTime.descending
```

### Filters

Filters can also be defined from a column.

Filters are chained using the `or` and `and` keywords:

```scala
val filter = $"Col1".asNumber >= 10 and $"Col2".asDate <= LocalDate.of(2022, 2, 2)
```

In order to apply a filter to a column you must first specify its property type as such:
- `$"myprop".asNumber`
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

For more information, you can check the [Notion documentation](https://developers.notion.com/reference/post-database-query).

## Update a database

Notion allows you to update:
- The database title
- The properties schema of the database

Strictly speaking, you have to provide a list of operations describing the list of changes from the current database to
the expected one.

There is two types of operations :
- Stateless operations are operations that does not require the current state of the database to generate a patch. As an
  example, `setDatabaseTitle` is a stateless operation because we don't need to explicitly know if the database already
  has a title or not.
- Stateful operations are operations that does require the current state of the database. As an example,
  `renameDatabase` requires the current database name to update it.

We explicitly differentiate the operations because stateless operations does not require a database to work. It means
that we don't have to retrieve the database first to update it. That is why the **Notion** interface provides several
update methods :

```scala
def updateDatabase(databaseId: String, operations: Database.Patch.StatelessOperations): IO[NotionError, Database]
def updateDatabase(database: Database, operations: Database.Patch.Operations): IO[NotionError, Database]
def updateDatabase(databaseId: String, operation: Database.Patch.Operations.Operation.Stateless): IO[NotionError, Database]
def updateDatabase(database: Database, operation: Database.Patch.Operations.Operation): IO[NotionError, Database]
```

We provide several kind of operations that can compose:

```scala
import zio.notion.dsl._ // We advise you to import the dsl

val operations = $$"col1".remove ++ $$"col2".patch.as(euro)
```

Here is a non-exhaustive list of operation:

```scala
val operation = setDatabaseTitle("Database title") // Set the database title (Stateless)
val operation = $$"col1".remove                    // Remove the col1 if it exists (Stateless)
val operation = $$"col1".create(euro)              // Create a new col1 with a number type in euro (Stateless)
val operation = $$"col1".patch.rename("col2")      // Rename the col1 to col2 (Stateful)
```

For more information, you can check the [Notion documentation](https://developers.notion.com/reference/update-a-database).
