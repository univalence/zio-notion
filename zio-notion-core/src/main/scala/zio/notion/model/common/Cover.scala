package zio.notion.model.common

import io.circe.generic.extras._

@ConfiguredJsonCodec sealed trait Cover

object Cover {
  final case class External(external: Url)  extends Cover
  final case class File(file: TemporaryUrl) extends Cover
}
