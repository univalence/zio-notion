package zio.notion.dsl

import zio.notion.model.database.query.{Filter, PropertyFilter, Query, Sorts}
import zio.notion.model.database.query.Sorts.Sorting
import zio.notion.model.database.query.Sorts.Sorting._
import zio.notion.model.database.query.Sorts.Sorting.TimestampType.{CreatedTime, LastEditedTime}

trait DatabaseQueryDSL {
  implicit def sortsToQuery(sorts: Sorts): Query    = Query(None, Some(sorts))
  implicit def filterToQuery(filter: Filter): Query = Query(Some(filter), None)

  // Sort helpers

  implicit def timestampTypeToSort(timestampType: TimestampType): Sorts = Timestamp(timestampType, ascending = true)
  implicit def columnToSort(column: Column): Sorts                      = Property(column.colName, ascending = true)
  implicit def sortingToSort(sorting: Sorting): Sorts                   = Sorts(List(sorting))

  implicit def timestampTypeToQuery(timestampType: TimestampType): Query = sortsToQuery(timestampTypeToSort(timestampType))
  implicit def columnToQuery(column: Column): Query                      = sortsToQuery(columnToSort(column))
  implicit def sortingToQuery(sorting: Sorting): Query                   = sortsToQuery(sortingToSort(sorting))

  val byCreatedTime: TimestampType    = CreatedTime
  val byLastEditedTime: TimestampType = LastEditedTime

  // Filter helpers

  implicit def propertyFilterToFilter(propertyFilter: PropertyFilter): Filter = Filter.One(propertyFilter)
}

object DatabaseQueryDSL extends DatabaseQueryDSL
