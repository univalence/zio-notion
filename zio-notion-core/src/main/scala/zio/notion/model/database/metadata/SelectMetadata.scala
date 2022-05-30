package zio.notion.model.database.metadata

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common.enumeration.BaseColor
import zio.notion.model.database.metadata.SelectMetadata.SelectOption

@ConfiguredJsonCodec final case class SelectMetadata(options: List[SelectOption])

object SelectMetadata {
  @ConfiguredJsonCodec final case class SelectOption(id: Option[String], name: String, color: Option[BaseColor])
}
