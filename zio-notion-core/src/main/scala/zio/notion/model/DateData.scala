package zio.notion.model

import io.circe.generic.extras._

import java.time.LocalDate

@ConfiguredJsonCodec final case class DateData(start: LocalDate, end: Option[LocalDate], timeZone: Option[String])
