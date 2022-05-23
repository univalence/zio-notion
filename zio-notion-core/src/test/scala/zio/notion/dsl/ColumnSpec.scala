package zio.notion.dsl

import zio.Scope
import zio.notion.model.database.patch.PatchPlan
import zio.test._

object ColumnSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Column suite")(
      test("I can generate a column using string context") {
        val column = $"col1"

        assertTrue(column == Column("col1"))
      },
      test("I can generate a column using col function") {
        val column = col("col1")

        assertTrue(column == Column("col1"))
      },
      test("I can convert a column into a column definition") {
        val columnDefinition = col("col1").definition

        assertTrue(columnDefinition == ColumnDefinition("col1", PatchPlan.unit))
      }
    )
}
