package zio.notion.dsl

import zio.Scope
import zio.notion.model.database.PatchedPropertyDefinition
import zio.test._

object ColumnDefinitionSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Column definition suite")(
      test("I can generate a column definition using string context") {
        val columnDefinition = $$"col1"

        assertTrue(columnDefinition == ColumnDefinition("col1"))
      },
      test("I can generate a column definition patch") {
        val patch = $$"col1".patch

        assertTrue(patch == PatchedColumnDefinition("col1", PatchedPropertyDefinition.unit))
      },
      test("I can generate a column definition using colDefinition function") {
        val columnDefinition = colDefinition("col1")

        assertTrue(columnDefinition == ColumnDefinition("col1"))
      }
    )
}
