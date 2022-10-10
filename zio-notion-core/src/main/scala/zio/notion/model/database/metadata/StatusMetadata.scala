package zio.notion.model.database.metadata

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common.enumeration.BaseColor
import zio.notion.model.database.metadata.StatusMetadata.{StatusGroup, StatusOption}

@ConfiguredJsonCodec final case class StatusMetadata(options: List[StatusOption], groups: List[StatusGroup])

object StatusMetadata {
  @ConfiguredJsonCodec final case class StatusOption(id: Option[String], name: String, color: Option[BaseColor])
  @ConfiguredJsonCodec final case class StatusGroup(id: Option[String], name: String, color: Option[BaseColor], optionIds: List[String])
}
