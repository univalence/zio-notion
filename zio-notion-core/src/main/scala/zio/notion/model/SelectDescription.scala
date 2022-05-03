package zio.notion.model

import io.circe.generic.extras.ConfiguredJsonCodec

object SelectDescription {
  @ConfiguredJsonCodec case class Select(options: List[SelectData])

  @ConfiguredJsonCodec final case class SelectData(id: Option[String], name: String, color: Option[BaseColor])

}
