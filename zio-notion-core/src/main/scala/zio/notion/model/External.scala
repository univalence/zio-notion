package zio.notion.model

import zio.json._

case class External(url: String)

object External {
  implicit val decoder: JsonDecoder[External] = DeriveJsonDecoder.gen[External]
  implicit val encoder: JsonEncoder[External] = DeriveJsonEncoder.gen[External]
}
