package zio.notion

import zio.json._

sealed trait NotionADT
final case class RichTextElement(
)

object NotionADT {
  type IdRequest = String

}
