package zio.notion.model.page.property.data

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec final case class Id(id: String)
