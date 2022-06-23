# Retrieve a block

To retrieve a block you can call the following function providing the id of the block:

```scala
for {
  user <- Notion.retrieveBlock("block-id")
} yield user
```

For more information, you can check the [notion documentation](https://developers.notion.com/reference/retrieve-a-block).
