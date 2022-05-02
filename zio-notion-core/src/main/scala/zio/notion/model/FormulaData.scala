package zio.notion.model

import io.circe.generic.extras._

@ConfiguredJsonCodec sealed trait FormulaData

object FormulaData {
  final case class String(string: Option[Predef.String])   extends FormulaData
  final case class Date(date: Option[DateData])            extends FormulaData
  final case class Number(number: Option[Double])          extends FormulaData
  final case class Boolean(boolean: Option[scala.Boolean]) extends FormulaData
}
