package zio.notion.model.common

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec final case class Url(url: String)
