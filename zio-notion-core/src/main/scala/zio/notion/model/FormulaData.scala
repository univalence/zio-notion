package zio.notion.model

import io.circe.generic.extras._

@ConfiguredJsonCodec sealed trait FormulaData

object FormulaData {
  final case class string(string: Option[String])    extends FormulaData
  final case class Date(date: Option[DateData])      extends FormulaData
  final case class Number(number: Option[Double])    extends FormulaData
  final case class boolean(boolean: Option[Boolean]) extends FormulaData
}
