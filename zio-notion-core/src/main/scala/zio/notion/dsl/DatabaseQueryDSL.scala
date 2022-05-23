package zio.notion.dsl

import zio.notion.model.database.query.{Filter, PropertyFilter, Sorts}
import zio.notion.model.database.query.Sorts.Sorting
import zio.notion.model.database.query.Sorts.Sorting._
import zio.notion.model.database.query.Sorts.Sorting.TimestampType.{CreatedTime, LastEditedTime}

trait DatabaseQueryDSL {

  // Sort helpers

  implicit def timestampTypeToSort(timestampType: TimestampType): Sorts = Timestamp(timestampType, ascending = true)

  implicit def columnToSorting(column: Column): Sorts = Property(column.colName, ascending = true)

  implicit def sortingToSort(sorting: Sorting): Sorts = Sorts(List(sorting))

  val createdTime: TimestampType    = CreatedTime
  val lastEditedTime: TimestampType = LastEditedTime

  // Filter helpers

  implicit def propertyFilterToFilter(propertyFilter: PropertyFilter): Filter = Filter.One(propertyFilter)
}

object DatabaseQueryDSL extends DatabaseQueryDSL
