package zio.notion.model.page.properties.data

import io.circe.generic.extras._

import zio.notion.model.common.enums.RollupFunction

@ConfiguredJsonCodec sealed trait RollupData

object RollupData {
  final case class Number(number: Option[Double], function: RollupFunction)          extends RollupData
  final case class Date(date: Option[DateData], function: RollupFunction)            extends RollupData
  final case class Array(array: List[RollupArrayDataType], function: RollupFunction) extends RollupData
}
