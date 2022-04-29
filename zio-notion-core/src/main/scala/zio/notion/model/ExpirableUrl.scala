package zio.notion.model

import io.circe.generic.extras._

import java.time.OffsetDateTime

@ConfiguredJsonCodec final case class ExpirableUrl(url: String, expiryTime: OffsetDateTime)
