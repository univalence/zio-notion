package zio.notion.model.database

import io.circe.syntax.EncoderOps

import zio.Scope
import zio.notion.dsl.DatabaseUpdateDSL._
import zio.notion.model.{database, printer}
import zio.test.{assertTrue, Spec, TestEnvironment, ZIOSpecDefault}

object PropertyDefinitionPatchSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Patched property description")(
      test("Test patched property description encoding") {
        val description: PropertyDefinitionPatch =
          database.PropertyDefinitionPatch(
            name           = Some("Test"),
            propertySchema = Some(multiSelect("test".blue, "test2"))
          )

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
