package zio.notion.model.page.property

import io.circe.generic.extras._

import zio.notion.model.common.{TemporaryUrl, Url}

@ConfiguredJsonCodec sealed trait Link

object Link {
  final case class File(name: String, file: TemporaryUrl) extends Link
  final case class External(name: String, external: Url)  extends Link
}
