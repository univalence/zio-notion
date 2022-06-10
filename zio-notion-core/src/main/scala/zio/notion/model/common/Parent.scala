package zio.notion.model.common

import io.circe.generic.extras._

import zio.notion.model.common.Parent.{DatabaseId, PageId}

@ConfiguredJsonCodec(decodeOnly = true) sealed trait Parent {

  implicit class StringOps(string: String) {
    def asParentPage: PageId         = PageId(string)
    def asParentDatabase: DatabaseId = DatabaseId(string)
  }
}

object Parent {
  @ConfiguredJsonCodec(encodeOnly = true) final case class PageId(pageId: String)         extends Parent
  @ConfiguredJsonCodec(encodeOnly = true) final case class DatabaseId(databaseId: String) extends Parent
  final case object Workspace                                                             extends Parent
}
