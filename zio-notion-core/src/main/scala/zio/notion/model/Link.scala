package zio.notion.model

import io.circe.generic.extras._

@ConfiguredJsonCodec sealed trait Link
final case class File(name: String, file: ExpirableUrl) extends Link
final case class External(name: String, external: Url)  extends Link
