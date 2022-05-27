# Retrieve a page

To retrieve a page you can call the following function providing the id of the page:

```scala
for {
  page <- Notion.retrievePage("page-id")
} yield page
```

This function does not retrieve the content of a page but only the metadata. We tend to follow the notion api, and they
don't provide this information when you retrieve a page. Instead, you have to call
[this endpoint](https://developers.notion.com/reference/get-block-children) which is not implement in ZIO Notion yet.

For more information, you can check the [notion documentation](https://developers.notion.com/reference/retrieve-a-database).