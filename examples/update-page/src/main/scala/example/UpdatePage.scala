package example

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
