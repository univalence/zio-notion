package zio.notion.model.common

import io.circe.generic.extras._

import zio.notion.model.common.Parent.{DatabaseId, PageId}

@ConfiguredJsonCodec sealed trait Parent {

  implicit class StringOps(string: String) {
    def asParentPage: PageId   = PageId(string)
    def asParentDB: DatabaseId = DatabaseId(string)
  }
}

object Parent {
  final case class PageId(pageId: String)         extends Parent
  final case class DatabaseId(databaseId: String) extends Parent
  final case object Workspace                     extends Parent
}
