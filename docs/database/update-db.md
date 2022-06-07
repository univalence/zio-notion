# Update a database

Notion allows you to update:
- The database title
- The properties schema of the database

For more information, you can check the [notion documentation](https://developers.notion.com/reference/update-a-database).

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
def updateDatabase(databaseId: String, operations: StatelessOperations): IO[NotionError, Database]
def updateDatabase(database: Database, operations: Operations): IO[NotionError, Database]
def updateDatabase(databaseId: String, operation: Operation.Stateless): IO[NotionError, Database]
def updateDatabase(database: Database, operation: Operation): IO[NotionError, Database]
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
val operation = $$"col1".patch.rename("col2")      // Rename the col1 to col2  (Stateful)
```