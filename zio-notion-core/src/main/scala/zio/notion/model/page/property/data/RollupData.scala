package zio.notion.model.page.property.data

import io.circe._
import io.circe.Decoder.Result
import io.circe.generic.extras._
import io.circe.syntax.EncoderOps

import zio.notion.model.common.enumeration.RollupFunction
import zio.notion.model.page.Property

sealed trait RollupData

object RollupData {
  @ConfiguredJsonCodec final case class Number(number: Option[Double], function: RollupFunction)                 extends RollupData
  @ConfiguredJsonCodec final case class Date(date: Option[Property.Date.Data], function: RollupFunction)         extends RollupData
  @ConfiguredJsonCodec final case class DateTime(date: Option[Property.DateTime.Data], function: RollupFunction) extends RollupData
  @ConfiguredJsonCodec final case class Array(array: List[RollupArrayDataType], function: RollupFunction)        extends RollupData

  implicit val formulaCodec: Codec[RollupData] =
    new Codec[RollupData] {

      override def apply(c: HCursor): Result[RollupData] =
        c.downField("type").as[scala.Predef.String] match {
          case Right(value) =>
            value match {
              case "number" => Decoder[Number].apply(c)
              case "date"   => Decoder[Date].apply(c) orElse Decoder[DateTime].apply(c)
              case "array"  => Decoder[Array].apply(c)
              case v        => Left(DecodingFailure(s"The type $v is unknown", c.history))
            }
          case Left(_) => Left(DecodingFailure(s"Missing required field 'type'", c.history))
        }

      override def apply(data: RollupData): Json =
        data match {
          case a: Number   => a.asJson deepMerge Json.obj("type" -> Json.fromString("number"))
          case a: Date     => a.asJson deepMerge Json.obj("type" -> Json.fromString("date"))
          case a: DateTime => a.asJson deepMerge Json.obj("type" -> Json.fromString("date"))
          case a: Array    => a.asJson deepMerge Json.obj("type" -> Json.fromString("array"))
        }
    }
}
