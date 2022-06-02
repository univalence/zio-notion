package zio.notion.model.page.property.data

import io.circe._
import io.circe.Decoder.Result
import io.circe.Decoder.decodeOffsetDateTime
import io.circe.Encoder.encodeOffsetDateTime
import io.circe.generic.extras.ConfiguredJsonCodec

import java.time.{LocalDate, OffsetDateTime, ZoneOffset}

@ConfiguredJsonCodec final case class DateData(start: OffsetDateTime, end: Option[OffsetDateTime], timeZone: Option[String])

object DateData {

  implicit val codec: Codec[OffsetDateTime] =
    new Codec[OffsetDateTime] {
      override def apply(a: OffsetDateTime): Json = encodeOffsetDateTime.apply(a)

      override def apply(c: HCursor): Result[OffsetDateTime] = {
        val dateDecoder: Decoder[OffsetDateTime]           = Decoder[LocalDate].map(_.atStartOfDay().atOffset(ZoneOffset.UTC))
        val OffsetDateTimeDecoder: Decoder[OffsetDateTime] = decodeOffsetDateTime

        (dateDecoder or OffsetDateTimeDecoder).apply(c)
      }
    }
}
