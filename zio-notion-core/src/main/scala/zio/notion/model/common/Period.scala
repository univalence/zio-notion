package zio.notion.model.common

import io.circe.generic.extras.ConfiguredJsonCodec

import java.time.LocalDate

@ConfiguredJsonCodec final case class Period(start: LocalDate, end: Option[LocalDate]) {

  override def toString: String =
    end match {
      case Some(end) => s"$start -> $end"
      case None      => s"$start"
    }
}
