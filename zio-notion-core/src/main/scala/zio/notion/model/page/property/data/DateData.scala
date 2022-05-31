package zio.notion.model.page.property.data

import io.circe._
import io.circe.Decoder.Result
import io.circe.Decoder.decodeZonedDateTime
import io.circe.Encoder.encodeZonedDateTime
import io.circe.generic.extras.ConfiguredJsonCodec

import java.time.{LocalDate, ZonedDateTime, ZoneOffset}

@ConfiguredJsonCodec final case class DateData(start: ZonedDateTime, end: Option[ZonedDateTime], timeZone: Option[String])

object DateData {

  implicit val codec: Codec[ZonedDateTime] =
    new Codec[ZonedDateTime] {
      override def apply(a: ZonedDateTime): Json = encodeZonedDateTime.apply(a)

      override def apply(c: HCursor): Result[ZonedDateTime] = {
        val dateDecoder: Decoder[ZonedDateTime]          = Decoder[LocalDate].map(_.atStartOfDay().atZone(ZoneOffset.UTC))
        val zonedDateTimeDecoder: Decoder[ZonedDateTime] = decodeZonedDateTime

        (dateDecoder or zonedDateTimeDecoder).apply(c)
      }
    }
}
