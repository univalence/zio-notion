package zio.notion.model.page.properties.data

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec final case class Id(id: String)
