package zio.notion.model.database.query

import io.circe.syntax._

import zio.Scope
import zio.notion.model.database.query.Sorts.Sorting.{Property, Timestamp}
import zio.notion.model.database.query.Sorts.Sorting.TimestampType.CreatedTime
import zio.notion.model.printer
import zio.test._

object SortSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Sort suite")(
      test("Sort serialization") {
        val sort: Sorts = Sorts(List(Property("checkbox", ascending = true), Timestamp(CreatedTime, ascending = false)))

        val expected: String =
          """[
            |  {
            |    "property" : "checkbox",
            |    "direction" : "ascending"
            |  },
            |  {
            |    "property" : "created_time",
            |    "direction" : "descending"
            |  }
            |]""".stripMargin

        assertTrue(printer.print(sort.asJson) == expected)
      },
      test("We can combine two sorts") {
        val left: Sorts  = Sorts(List(Property("checkbox", ascending = true)))
        val right: Sorts = Sorts(List(Timestamp(CreatedTime, ascending = false)))

        val sort: Sorts = Sorts(List(Property("checkbox", ascending = true), Timestamp(CreatedTime, ascending = false)))

        assertTrue(left.andThen(right) == sort)
      },
      test("We can combine a sort with a timestamp") {
        val left: Sorts      = Sorts(List(Property("checkbox", ascending = true)))
        val right: Timestamp = Timestamp(CreatedTime, ascending = false)

        val sort: Sorts = Sorts(List(Property("checkbox", ascending = true), Timestamp(CreatedTime, ascending = false)))

        assertTrue(left.andThen(right) == sort)
      }
    )
}
