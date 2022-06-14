package zio.notion.model.common

import io.circe.generic.extras._

@ConfiguredJsonCodec(decodeOnly = true) sealed trait Parent

object Parent {

  implicit class StringOps(string: String) {
    def asParentPage: PageId         = PageId(string)
    def asParentDatabase: DatabaseId = DatabaseId(string)
  }

  @ConfiguredJsonCodec(encodeOnly = true) final case class PageId(pageId: String)         extends Parent
  @ConfiguredJsonCodec(encodeOnly = true) final case class DatabaseId(databaseId: String) extends Parent
  final case object Workspace                                                             extends Parent
}
