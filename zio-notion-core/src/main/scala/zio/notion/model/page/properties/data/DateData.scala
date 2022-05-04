package zio.notion.model.page.properties.data

import io.circe.generic.extras.ConfiguredJsonCodec

import java.time.LocalDate

@ConfiguredJsonCodec final case class DateData(start: LocalDate, end: Option[LocalDate], timeZone: Option[String])
