package zio.notion.model.page.properties.data

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common.enumeration.BaseColor

@ConfiguredJsonCodec final case class SelectData(id: String, name: String, color: BaseColor)
