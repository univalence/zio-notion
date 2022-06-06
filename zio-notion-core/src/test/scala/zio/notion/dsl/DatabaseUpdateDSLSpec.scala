package zio.notion.dsl

import zio.Scope
import zio.notion.model.common.enumeration.Color
import zio.notion.model.database.PatchedPropertyDefinition.PropertySchema.SelectOption
import zio.test.{assertTrue, Spec, TestEnvironment, ZIOSpecDefault}

object DatabaseUpdateDSLSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("DB update dsl test suite")(
      test("should be able to render colored select options from stringOps") {
        val selectOpts =
          List(
            "opt1".gray,
            "opt1".brown,
            "opt1".orange,
            "opt1".yellow,
            "opt1".green,
            "opt1".blue,
            "opt1".purple,
            "opt1".pink,
            "opt1".red
          )

        assertTrue(
          selectOpts == List(
            SelectOption("opt1", Some(Color.Gray)),
            SelectOption("opt1", Some(Color.Brown)),
            SelectOption("opt1", Some(Color.Orange)),
            SelectOption("opt1", Some(Color.Yellow)),
            SelectOption("opt1", Some(Color.Green)),
            SelectOption("opt1", Some(Color.Blue)),
            SelectOption("opt1", Some(Color.Purple)),
            SelectOption("opt1", Some(Color.Pink)),
            SelectOption("opt1", Some(Color.Red))
          )
        )
      }
    )
}
