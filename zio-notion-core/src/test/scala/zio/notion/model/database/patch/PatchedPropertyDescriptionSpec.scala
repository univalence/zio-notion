package zio.notion.model.database.patch

import io.circe.syntax.EncoderOps

import zio.Scope
import zio.notion.model.common.enumeration.Color
import zio.notion.model.database.patch.PatchedPropertyDescription.PropertyType
import zio.notion.model.database.patch.PatchedPropertyDescription.PropertyType.SelectOption
import zio.notion.model.printer
import zio.test._

object PatchedPropertyDescriptionSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Patched property description")(
      test("Test patched property description encoding") {
        val description: PatchedPropertyDescription =
          PatchedPropertyDescription
            .rename("Test")
            .cast(PropertyType.MultiSelect(List(SelectOption("test", Some(Color.Blue)), SelectOption("test2", None))))

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
