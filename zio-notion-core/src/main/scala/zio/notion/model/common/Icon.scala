package zio.notion.model.common

import io.circe.generic.extras._

@ConfiguredJsonCodec sealed trait Icon

object Icon {
  final case class Emoji(emoji: String)     extends Icon
  final case class External(external: Url)  extends Icon
  final case class File(file: TemporaryUrl) extends Icon
}
