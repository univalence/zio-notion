package example

import zio._
import zio.notion._
import zio.notion.dsl._

object AppendBlocks extends ZIOAppDefault {
  val apiToken: String = "secret_XXXX" // Insert your own bearer
  val blockId: String  = "XXX"         // Insert your own page or database

  val blocks =
    List(
      h1"My beautiful documentation",
      h2"My subtitle",
      p"Lorem ipsum for the win !"
    )

  def example: ZIO[Notion, NotionError, Unit] = Notion.appendBlocks(blockId, blocks).unit

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = example.provide(Notion.layerWith(apiToken))
}
