package zio.notion.dsl

import zio.Scope
import zio.notion.dsl.query._
import zio.notion.model.database.query.Sorts
import zio.notion.model.database.query.Sorts.Sorting.{Property, Timestamp}
import zio.notion.model.database.query.Sorts.Sorting.TimestampType.{CreatedTime, LastEditedTime}
import zio.test._

object SortSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Sort dsl helper functions suite")(
      test("We can use a String as a Sort") {
        val sort: Sorts     = "checkbox"
        val expected: Sorts = Sorts(List(Property("checkbox", ascending = true)))
        assertTrue(sort == expected)
      },
      test("We can use ascending function on String") {
        val sort: Sorts     = "checkbox".ascending
        val expected: Sorts = Sorts(List(Property("checkbox", ascending = true)))
        assertTrue(sort == expected)
      },
      test("We can use descending function on String") {
        val sort: Sorts     = "checkbox".descending
        val expected: Sorts = Sorts(List(Property("checkbox", ascending = false)))
        assertTrue(sort == expected)
      },
      test("We can use a Sorting as a Sort") {
        val sort: Sorts     = Property("checkbox", ascending = false)
        val expected: Sorts = Sorts(List(Property("checkbox", ascending = false)))
        assertTrue(sort == expected)
      },
      test("We can use createdTime as a Sorting") {
        val sort: Sorts     = createdTime
        val expected: Sorts = Sorts(List(Timestamp(CreatedTime, ascending = true)))
        assertTrue(sort == expected)
      },
      test("We can use lastEditedTime as a Sorting") {
        val sort: Sorts     = lastEditedTime
        val expected: Sorts = Sorts(List(Timestamp(LastEditedTime, ascending = true)))
        assertTrue(sort == expected)
      }
    )
}
