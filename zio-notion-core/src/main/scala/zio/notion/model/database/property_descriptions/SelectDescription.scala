package zio.notion.model.database.property_descriptions

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common.enums.BaseColor

object SelectDescription {
  @ConfiguredJsonCodec final case class Select(options: List[SelectData])

  @ConfiguredJsonCodec final case class SelectData(id: Option[String], name: String, color: Option[BaseColor])

}
