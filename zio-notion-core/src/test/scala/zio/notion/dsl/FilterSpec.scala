package zio.notion.dsl

import zio.Scope
import zio.notion.dsl.query._
import zio.notion.model.database.query.Filter
import zio.notion.model.database.query.PropertyFilter.TextPropertyFilter.StartsWith
import zio.notion.model.database.query.PropertyFilter.Title
import zio.test._

object FilterSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Filter dsl helper functions suite")(
      test("We can use a PropertyFilter as a Filter") {
        val filter: Filter   = title("Title").startsWith("Toto")
        val expected: Filter = Filter.One(Title("Title", StartsWith("Toto")))

        assertTrue(filter == expected)
      }
    )
}
