<h1 align="center">ZIO Notion</h1>

<p align="center">
  <a href="https://img.shields.io/badge/Project%20Stage-Development-yellowgreen.svg">
    <img src="https://img.shields.io/badge/Project%20Stage-Development-yellowgreen.svg" />
  </a>
  <a href="https://github.com/univalence/zio-notion/actions">
    <img src="https://github.com/univalence/zio-notion/actions/workflows/ci.yml/badge.svg" />
  </a>
  <a href="https://codecov.io/gh/univalence/zio-notion">
    <img src="https://codecov.io/gh/univalence/zio-notion/branch/master/graph/badge.svg" />
  </a>
  <a href="https://scala-steward.org">
    <img src="https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=" />
  </a>
  <a href="https://index.scala-lang.org/univalence/zio-notion/zio-notion">
    <img src="https://index.scala-lang.org/univalence/zio-notion/zio-notion/latest-by-scala-version.svg?platform=jvm" />
  </a>
</p>

<p align="center">
    A strongly typed interface to interact with Notion using ZIO
</p>

## Documentation

You can find the documentation of zio-notion [here](https://univalence.github.io/zio-notion/).

## Example

```scala
import zio._
import zio.notion._
import zio.notion.dsl._
import zio.notion.model.page.Page

import java.time.LocalDate

object UpdatePage extends ZIOAppDefault {
  def buildPatch(page: Page): Either[NotionError, Page.Patch] = {
    val date = LocalDate.of(2022, 2, 2)

    for {
      patch0 <- Right(page.patch)
      patch1 <- patch0.updateProperty($"col1".asNumber.patch.ceil)
      patch2 <- patch1.updateProperty($"col2".asDate.patch.between(date, date.plusDays(14)))
    } yield patch2.archive
  }

  def example: ZIO[Notion, NotionError, Unit] =
    for {
      page  <- Notion.retrievePage("6A074793-D735-4BF6-9159-24351D239BBC") // Insert your own page ID
      patch <- ZIO.fromEither(buildPatch(page))
      _     <- Notion.updatePage(patch)
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example.provide(Notion.layerWith("6A074793-D735-4BF6-9159-24351D239BBC")) // Insert your own bearer
}
```

## Features

### Page

- You can update a page
- You can retrieve a page
- You can archive a page
- ~~You can create a page~~ 🕦 COMING SOON

### Database

- You can update a database
- You can retrieve a database
- You can query a database
- You can create a database

### Block

🕦 COMING SOON

### User

- You can retrieve a user
- ~~You can list users~~ 🕦 COMING SOON
- ~~You can retrieve your token's bot user~~ 🕦 COMING SOON

### Search

🕦 COMING SOON

## Latest version

If you want to get the very last version of this library you can still download it using:

```scala
libraryDependencies += "io.univalence" %% "zio-notion" % "0.2.0"
```

### Snapshots

If you want to get the latest snapshots (the version associated with the last commit on master), you can still download
it using:

```scala
resolvers += Resolver.sonatypeRepo("snapshots"),
libraryDependencies += "io.univalence" %% "zio-notion" % "<SNAPSHOT-VERSION>"
```

You can find the latest version on
[nexus repository manager](https://oss.sonatype.org/#nexus-search;gav~io.univalence~zio-notion_2.13~~~~kw,versionexpand).

## Contributions

Pull requests are welcomed. We are open to organize pair-programming session to tackle improvements. If you want to add
new things in `zio-notion`, don't hesitate to open an issue!
