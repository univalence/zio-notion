package zio.notion.dsl

import zio.Scope
import zio.test._

object ColumnSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Colmun suite")(
      test("I can generate a column using string context") {
        val column = $"col1"

        assertTrue(column == Column("col1"))
      },
      test("I can generate a column using col function") {
        val column = col("col1")

        assertTrue(column == Column("col1"))
      }
    )
}
