# Retrieve a database

Retrieve a database is useful if you want to retrieve all the properties (the metadata) of a database.

To retrieve a retrieve you can call the following function providing the id of the database:

```scala
for {
  database <- Notion.retrieveDatabase("database-id")
} yield database
```

The **Database** type provides you with several properties and methods.

You can retrieve the following kind or properties:
- createdTime
- lastEditedTime
- createdBy
- lastEditedBy
- id
- title
- cover
- icon
- parent
- archived
- properties
- url

The **Database** type provides you with patching functions described in the patch a `How to patch a database` section.

For more information, you can check the [notion documentation](https://developers.notion.com/reference/retrieve-a-database).