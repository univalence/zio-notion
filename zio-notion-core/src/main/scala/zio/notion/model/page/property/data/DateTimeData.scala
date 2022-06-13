package zio.notion.model.page.property.data

import io.circe.generic.extras.ConfiguredJsonCodec

import java.time.OffsetDateTime

@ConfiguredJsonCodec final case class DateTimeData(start: OffsetDateTime, end: Option[OffsetDateTime], timeZone: Option[String])
