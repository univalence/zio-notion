package zio.notion.model.common

import io.circe.generic.extras._

import java.util.UUID

@ConfiguredJsonCodec sealed trait Parent

object Parent {
  final case class PageId(pageId: UUID)         extends Parent
  final case class DatabaseId(databaseId: UUID) extends Parent
  final case object Workspace                   extends Parent
}
