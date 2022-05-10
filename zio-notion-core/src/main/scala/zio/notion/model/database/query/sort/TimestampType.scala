package zio.notion.model.database.query.sort

import zio.notion.model.database.query.sort.Sort.Timestamp

sealed trait TimestampType {
  self =>
  def ascending: Sort  = Timestamp(self, ascending = true)
  def descending: Sort = Timestamp(self, ascending = false)
}

object TimestampType {
  final case object CreatedTime    extends TimestampType
  final case object LastEditedTime extends TimestampType

  val createdTime: TimestampType    = CreatedTime
  val lastEditedTime: TimestampType = LastEditedTime
}
