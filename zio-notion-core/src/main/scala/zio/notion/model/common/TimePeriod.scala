package zio.notion.model.common

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@ConfiguredJsonCodec final case class TimePeriod(start: OffsetDateTime, end: Option[OffsetDateTime], timeZone: Option[String]) {
  def toPeriod: Period = common.Period(start.toLocalDate, end.map(_.toLocalDate))

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
