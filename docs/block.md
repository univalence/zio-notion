---
slug: /block
sidebar_position: 4
---

# Block

A `Block` is a fragment of a page content. Basically, every page in Notion are composed by different type of blocks such
as h1, image and so on.

## Retrieve a block

To retrieve a block you can call the following function providing the id of the block:

```scala
val block: ZIO[Notion, NotionError, Block] = Notion.retrieveBlock("block-id")
```

Generally, you won't retrieve a block but the blocks that composed a particular page. You can retrieve them using:

```scala
import zio.notion._

val blocks: ZIO[Notion, NotionError, Blocks] = retrieveAllBlocks("page-id")
```

For more information, you can check the 
[Notion documentation](https://developers.notion.com/reference/retrieve-a-block).

## Append blocks

You can append blocks from a page or a block providing a list of block contents:

```scala
import zio.notion._
import zio.notion.dsl._ // We advice you to use this import to write block contents

val blockId = "XXXXX" // It can be either a page or a block Id
val blocks = List(
  h1"My beautiful documentation",
  h2"My subtitle",
  p"Lorem ipsum for the win !"
)

val block: ZIO[Notion, NotionError, Blocks] = Notion.appendBlocks(blockId, blocks)
```

For more information, you can check the 
[Notion documentation](https://developers.notion.com/reference/patch-block-children).
