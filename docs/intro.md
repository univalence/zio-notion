---
slug: /
sidebar_position: 1
---

# Introduction

## Getting started

ZIO Notion is a library that allow user to interact with Notion in a functional way. Under the hood, the library uses
STTP, Circe and ZIO to communicate with the Notion API. Thus, we tend to follow the Notion API spec as much as possible.

To install the library, add the following line to your **build.sbt**:

```scala
libraryDependencies += "io.univalence" %% "zio-notion" % "0.9.0"
```

You will need to create a Notion integration to use this library. You can rapidly create one integration for free at
https://www.notion.so/my-integrations.

You should retrieve a bearer token that will allow your bot to communicate with Notion.

When you have the token you can then start to use the library by creating the live layer such as:

```scala
import zio.notion._

val notionLayer: Layer[Throwable, Notion] = Notion.layerWith("6A074793-D735-4BF6-9159-24351D239BBC")
```

If you are not comfortable with ZIO's layers, we advise you to read 
[this documentation](https://zio.dev/next/datatypes/contextual/zlayer).

When it is done, you can start interacting with the Notion API:

```scala
import zio.notion._

val retrievePageId: ZIO[Notion, NotionError, String] = for {
  page <- Notion.retrievePage("page-id")
} yield page.id
```
## Base methods

Here are the base methods that you can use to interact with Notion

| Method                   | Parameters                                                                                                                                    | Return type                          |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| **retrievePage**         | pageId: `String`                                                                                                                              | `IO`[`NotionError`, `Page`]          |
| **retrieveDatabase**     | databaseId: `String`                                                                                                                          | `IO`[`NotionError`, `Database`]      |
| **retrieveUser**         | userId: `String`                                                                                                                              | `IO`[`NotionError`, `User`]          |
| **retrieveUsers**        | pagination: `Pagination`                                                                                                                      | `IO`[`NotionError`, `Users`]         |
| **retrieveBlock**        | blockId: `String`                                                                                                                             | `IO`[`NotionError`, `Block`]         |
| **retrieveBlocks**       | pageId: `String`, pagination: `Pagination`                                                                                                    | `IO`[`NotionError`, `Blocks`]        |
| **queryDatabase**        | databaseId: `String`, query: `Query`, pagination: `Pagination`                                                                                | `IO`[`NotionError`, `DatabaseQuery`] |
| **updatePage**           | pageId: `String`, operations: `Page.Patch.StatelessOperations`                                                                                | `IO`[`NotionError`, `Page`]          |
| **updatePage**           | page: `Page`, operations: `Page.Patch.Operations`                                                                                             | `IO`[`NotionError`, `Page`]          |
| **updateDatabase**       | databaseId: `String`, operations: `Database.Patch.StatelessOperations`                                                                        | `IO`[`NotionError`, `Database`]      |
| **updateDatabase**       | database: `Database`, operations: `Database.Patch.Operations`                                                                                 | `IO`[`NotionError`, `Database`]      |
| **createDatabase**       | pageId: `String`, title: `Seq[RichTextFragment]`, icon: `Option[Icon]`, cover: `Option[Cover]`, properties: `Map[String, PropertySchema]`     | `IO`[`NotionError`, `Database`]      |
| **createPageInPage**     | parent: `PageId`, title: `Option[PatchedProperty]`, icon: `Option[Icon]`, cover: `Option[Cover]`, children: `Seq[BlockContent]`               | `IO`[`NotionError`, `Page`]          |
| **createPageInDatabase** | parent: `DatabaseId`, properties: `Map[String, PatchedProperty]`, icon: `Option[Icon]`, cover: `Option[Cover]`, children: `Seq[BlockContent]` | `IO`[`NotionError`, `Page`]          |

Feel free to read the `Tutorials` section for examples on how to use `zio-notion` or visit the [examples directory on github](https://github.com/univalence/zio-notion/tree/master/examples/)




