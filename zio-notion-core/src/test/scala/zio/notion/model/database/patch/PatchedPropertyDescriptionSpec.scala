package zio.notion.model.database.patch

import io.circe.syntax.EncoderOps

import zio.Scope
import zio.notion.dsl.DatabaseUpdateDSL._
import zio.notion.model.printer
import zio.test._

object PatchedPropertyDescriptionSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Patched property description")(
      test("Test patched property description encoding") {
        val description: PatchedPropertyDescription =
          PatchedPropertyDescription
            .rename("Test")
            .cast(asMultiSelect("test".blue, "test2"))

        val expected: String =
          """{
            |  "name" : "Test",
            |  "multi_select" : {
            |    "options" : [
            |      {
            |        "name" : "test",
            |        "color" : "blue"
            |      },
            |      {
            |        "name" : "test2"
            |      }
            |    ]
            |  }
            |}""".stripMargin

        assertTrue(printer.print(description.asJson) == expected)
      }
    )

}
