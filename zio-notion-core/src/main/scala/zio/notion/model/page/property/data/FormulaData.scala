package zio.notion.model.page.property.data

import io.circe._
import io.circe.Decoder.Result
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.syntax.EncoderOps

sealed trait FormulaData

object FormulaData {
  @ConfiguredJsonCodec final case class String(string: Option[Predef.String])   extends FormulaData
  @ConfiguredJsonCodec final case class Date(date: Option[DateData])            extends FormulaData
  @ConfiguredJsonCodec final case class DateTime(date: Option[DateTimeData])    extends FormulaData
  @ConfiguredJsonCodec final case class Number(number: Option[Double])          extends FormulaData
  @ConfiguredJsonCodec final case class Boolean(boolean: Option[scala.Boolean]) extends FormulaData

  implicit val formulaCodec: Codec[FormulaData] =
    new Codec[FormulaData] {

      override def apply(c: HCursor): Result[FormulaData] =
        c.downField("type").as[scala.Predef.String] match {
          case Right(value) =>
            value match {
              case "string"  => Decoder[String].apply(c)
              case "date"    => Decoder[Date].apply(c) orElse Decoder[DateTime].apply(c)
              case "number"  => Decoder[Number].apply(c)
              case "boolean" => Decoder[Boolean].apply(c)
              case v         => Left(DecodingFailure(s"The type $v is unknown", c.history))
            }
          case Left(_) => Left(DecodingFailure(s"Missing required field 'type'", c.history))
        }

      override def apply(data: FormulaData): Json =
        data match {
          case a: String   => a.asJson deepMerge Json.obj("type" -> Json.fromString("string"))
          case a: Date     => a.asJson deepMerge Json.obj("type" -> Json.fromString("date"))
          case a: DateTime => a.asJson deepMerge Json.obj("type" -> Json.fromString("date"))
          case a: Number   => a.asJson deepMerge Json.obj("type" -> Json.fromString("number"))
          case a: Boolean  => a.asJson deepMerge Json.obj("type" -> Json.fromString("boolean"))
        }
    }
}
