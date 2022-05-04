package zio.notion.model

import io.circe.generic.extras.ConfiguredJsonCodec

object SelectDescription {
  @ConfiguredJsonCodec final case class Select(options: List[SelectData])

  @ConfiguredJsonCodec final case class SelectData(id: Option[String], name: String, color: Option[BaseColor])

}
