package zio.notion.model.common

import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.model.common

import java.time.OffsetDateTime

@ConfiguredJsonCodec final case class TimePeriod(start: OffsetDateTime, end: Option[OffsetDateTime], timeZone: Option[String]) {
  def toPeriod: Period = common.Period(start.toLocalDate, end.map(_.toLocalDate))

  override def toString: String =
    end match {
      case Some(end) => s"$start -> $end"
      case None      => s"$start"
    }
}
