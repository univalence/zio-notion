package zio.notion.model

import io.circe.generic.extras._

@ConfiguredJsonCodec final case class SelectData(id: String, name: String, color: BaseColor)
