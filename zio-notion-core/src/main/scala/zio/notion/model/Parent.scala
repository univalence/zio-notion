package zio.notion.model

import io.circe.generic.extras._

@ConfiguredJsonCodec sealed trait Parent

object Parent {
  final case class PageId(pageId: String)         extends Parent
  final case class DatabaseId(databaseId: String) extends Parent
  final case object Workspace                     extends Parent
}
