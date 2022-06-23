# Retrieve a database

The `Notion.retrieveDatabase` function retrieve the metadata of a Notion database.

You can use it as follows:

```scala
for {
  database <- Notion.retrieveDatabase("database-id")
} yield database
```

The **Database** type provides the following properties:
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

For more information, you can check the [notion documentation](https://developers.notion.com/reference/retrieve-a-database).