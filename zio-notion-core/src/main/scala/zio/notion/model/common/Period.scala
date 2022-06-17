package zio.notion.model.common

import io.circe.generic.extras.ConfiguredJsonCodec

import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ConfiguredJsonCodec final case class Period(start: LocalDate, end: Option[LocalDate]) {

  override def toString: String = {
    val startDate = start.format(DateTimeFormatter.ISO_DATE_TIME)
    end match {
      case Some(end) =>
        val endDate = end.format(DateTimeFormatter.ISO_DATE_TIME)
        s"$startDate -> $endDate"
      case None => startDate
    }
  }
}
