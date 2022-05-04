package zio.notion.model.common

import io.circe.generic.extras.ConfiguredJsonCodec

import java.time.OffsetDateTime

@ConfiguredJsonCodec final case class TemporaryUrl(url: String, expiryTime: OffsetDateTime)
