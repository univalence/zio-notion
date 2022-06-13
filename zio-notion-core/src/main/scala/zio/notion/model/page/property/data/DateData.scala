package zio.notion.model.page.property.data

import io.circe.generic.extras.ConfiguredJsonCodec

import java.time.LocalDate

@ConfiguredJsonCodec final case class DateData(start: LocalDate, end: Option[LocalDate])
