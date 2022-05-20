package example

import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend

import zio._
import zio.notion._
import zio.notion.model.page.Page
import zio.notion.model.page.patch.PatchedProperty._

import java.time.LocalDate

object UpdatePage extends ZIOAppDefault {
  val notionConfiguration: NotionConfiguration =
    NotionConfiguration(
      bearer = "6A074793-D735-4BF6-9159-24351D239BBC" // Insert your own bearer
    )

  def buildPatch(page: Page): Either[NotionError, Page.Patch] = {
    val date = LocalDate.of(2022, 2, 2)

    for {
      patch0 <- Right(page.patch)
      patch1 <- patch0.updateProperty(PatchedNumber.ceil.on("Col1"))
      patch2 <- patch1.updateProperty(PatchedDate.between(date, date.plusDays(14)).on("Col2"))
    } yield patch2.archive
  }

  def example: ZIO[Notion, NotionError, Unit] =
    for {
      page  <- Notion.retrievePage("6A074793-D735-4BF6-9159-24351D239BBC") // Insert your own page ID
      patch <- ZIO.fromEither(buildPatch(page))
      _     <- Notion.updatePage(patch)
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example.provide(
      AsyncHttpClientZioBackend.layer(),
      notionConfiguration.asLayer,
      NotionClient.live,
      Notion.live
    )
}
