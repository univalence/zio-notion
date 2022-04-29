package zio.notion.model

import zio.json._

import java.time.OffsetDateTime

case class File(url: String, @jsonField("expiry_time") expiryTime: OffsetDateTime)

object File {
  implicit val decoder: JsonDecoder[File] = DeriveJsonDecoder.gen[File]
  implicit val encoder: JsonEncoder[File] = DeriveJsonEncoder.gen[File]
}
