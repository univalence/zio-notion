package zio.notion.model.common.richtext

import io.circe.parser.decode

import zio.Scope
import zio.notion.model.common.enumeration.Color
import zio.notion.model.common.richtext
import zio.test.{assert, TestEnvironment, ZIOSpecDefault, ZSpec}
import zio.test.Assertion.{equalTo, isRight}

object AnnotationsSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("Annotations serde suite")(
      test("We should be able to parse an annotations as json") {
        val json: String =
          s"""{
             |    "bold": true,
             |    "italic": false,
             |    "strikethrough": false,
             |    "underline": true,
             |    "code": false,
             |    "color": "blue"
             |}""".stripMargin

        val expected: Annotations =
          richtext.Annotations(
            bold          = true,
            italic        = false,
            strikethrough = false,
            underline     = true,
            code          = false,
            color         = Color.Blue
          )

        assert(decode[Annotations](json))(isRight(equalTo(expected)))
      }
    )
}
