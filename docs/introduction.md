---
slug: /
sidebar_position: 1
---

# Introduction

ZIO Notion is a library that allow user to interact with Notion in a functional way. Under the hood, the library uses
STTP, Circe and ZIO to communicate with the Notion API. Thus, we tend to follow the Notion API spec as much as we can.

To install the library, add the following line to your **build.sbt**:

```scala
libraryDependencies += "io.univalence" %% "zio-notion" % "0.9.2"
```

You will need to create a Notion integration to use this library. You can rapidly create one integration for free at
https://www.notion.so/my-integrations. When it is done, you should retrieve the Internal Integration Token provided by
Notion. When you have the token you can then start to use the library by creating the live layer such as:

```scala
import zio.notion._

val notionLayer: Layer[Throwable, Notion] = Notion.layerWith("6A074793-D735-4BF6-9159-24351D239BBC")
```

:::caution

We advise you to use [zio-config](https://github.com/zio/zio-config) to insert the token. This token can be used by
malicious people, to access your Notion data !

:::

If you are not comfortable with ZIO's layers, we advise you to read 
[this documentation](https://zio.dev/next/datatypes/contextual/zlayer).

When it is done, you can start interacting with the Notion API:

````scala
import zio.notion._

val retrievePageId: ZIO[Notion, NotionError, String] = for {
  page <- Notion.retrievePage("page-id")
} yield page.id
````