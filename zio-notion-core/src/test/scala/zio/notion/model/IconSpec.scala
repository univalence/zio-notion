package zio.notion.model

import zio.Scope
import zio.json._
import zio.notion.Faker._
import zio.notion.model.{External => BaseExternal, File => BaseFile}
import zio.notion.model.Icon._
import zio.test._
import zio.test.Assertion._

object IconSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("Icon Serde Suite")(
      test("We should be able to parse an emoji icon as json") {
        val json: String =
          """{
            |  "type": "emoji",
            |  "emoji": "🎉"
            |}""".stripMargin

        val expected: Emoji = Emoji(emoji = "🎉")

        assert(json.fromJson[Icon])(isRight(equalTo(expected)))
      },
      test("We should be able to parse an external icon as json") {
        val json: String =
          s"""{
             |  "type": "external",
             |  "external": {
             |     "url": $fakeUrl
             |  }
             |}""".stripMargin

        val expected: External = External(external = BaseExternal(fakeUrl))

        assert(json.fromJson[Icon])(isRight(equalTo(expected)))
      },
      test("We should be able to parse a file icon as json") {
        val json: String =
          s"""{
             |  "type": "file",
             |  "file": {
             |     "url": $fakeUrl,
             |     "expiry_time": $fakeDatetime
             |  }
             |}""".stripMargin

        val expected: File = File(file = BaseFile(fakeUrl, fakeDatetime))

        assert(json.fromJson[Icon])(isRight(equalTo(expected)))
      }
    )
}
