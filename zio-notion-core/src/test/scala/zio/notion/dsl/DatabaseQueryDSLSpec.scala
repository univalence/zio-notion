package zio.notion.dsl

import zio.Scope
import zio.notion.model.database.query.{Filter, Sorts}
import zio.notion.model.database.query.PropertyFilter.{Date, Title}
import zio.notion.model.database.query.PropertyFilter.DatePropertyFilter.Before
import zio.notion.model.database.query.PropertyFilter.TextPropertyFilter.StartsWith
import zio.notion.model.database.query.Sorts.Sorting.{Property, Timestamp}
import zio.notion.model.database.query.Sorts.Sorting.TimestampType.{CreatedTime, LastEditedTime}
import zio.test._

import java.time.LocalDate

object DatabaseQueryDSLSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = filterSpec + sortsSpec

  def filterSpec: Spec[TestEnvironment with Scope, Any] =
    suite("Filter dsl helper functions suite")(
      test("We can use a PropertyFilter as a Filter") {
        val filter: Filter   = $"Title".asTitle.startsWith("Toto")
        val expected: Filter = Filter.One(Title("Title", StartsWith("Toto")))

        assertTrue(filter == expected)
      },
      test("We can use a dsl to express date filters") {
        val filter: Date = $"Date".asDate.before(LocalDate.of(2022, 2, 2))

        assertTrue(filter.date == Before("2022-02-02"))
      }
    )

  def sortsSpec: Spec[TestEnvironment with Scope, Any] =
    suite("Sort dsl helper functions suite")(
      test("We can use a String as a Sort") {
        val sort: Sorts     = $"checkbox"
        val expected: Sorts = Sorts(List(Property("checkbox", ascending = true)))
        assertTrue(sort == expected)
      },
      test("We can use ascending function on String") {
        val sort: Sorts     = $"checkbox".ascending
        val expected: Sorts = Sorts(List(Property("checkbox", ascending = true)))
        assertTrue(sort == expected)
      },
      test("We can use descending function on String") {
        val sort: Sorts     = $"checkbox".descending
        val expected: Sorts = Sorts(List(Property("checkbox", ascending = false)))
        assertTrue(sort == expected)
      },
      test("We can use a Sorting as a Sort") {
        val sort: Sorts     = Property("checkbox", ascending = false)
        val expected: Sorts = Sorts(List(Property("checkbox", ascending = false)))
        assertTrue(sort == expected)
      },
      test("We can use createdTime as a Sorting") {
        val sort: Sorts     = byCreatedTime
        val expected: Sorts = Sorts(List(Timestamp(CreatedTime, ascending = true)))
        assertTrue(sort == expected)
      },
      test("We can use lastEditedTime as a Sorting") {
        val sort: Sorts     = byLastEditedTime
        val expected: Sorts = Sorts(List(Timestamp(LastEditedTime, ascending = true)))
        assertTrue(sort == expected)
      }
    )
}
