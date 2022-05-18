package zio.notion.dsl

import zio.Scope
import zio.notion.dsl.query._
import zio.notion.model.database.query.Filter
import zio.notion.model.database.query.PropertyFilter.{Date, Title}
import zio.notion.model.database.query.PropertyFilter.DatePropertyFilter.Before
import zio.notion.model.database.query.PropertyFilter.TextPropertyFilter.StartsWith
import zio.test._

import java.time.LocalDate

object FilterSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Filter dsl helper functions suite")(
      test("We can use a PropertyFilter as a Filter") {
        val filter: Filter   = title("Title").startsWith("Toto")
        val expected: Filter = Filter.One(Title("Title", StartsWith("Toto")))

        assertTrue(filter == expected)
      },
      test("We can use a dsl to express date filters") {
        val filter: Date = date("Date").before(LocalDate.of(2022, 2, 2))

        assertTrue(filter.date == Before("2022-02-02"))
      }
    )
}
