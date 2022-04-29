package zio.notion.model
import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.Icon._
import zio.test._
import zio.test.Assertion._

object IconSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("Icon serde suite")(
      test("We should be able to parse an emoji icon as json") {
        val json: String =
          s"""{
             |  "type": "emoji",
             |  "emoji": "$fakeEmoji"
             |}""".stripMargin

        val expected: Emoji = Emoji(emoji = fakeEmoji)

        assert(decode[Icon](json))(isRight(equalTo(expected)))
      },
      test("We should be able to parse an external icon as json") {
        val json: String =
          s"""{
             |  "type": "external",
             |  "external": {
             |     "url": "$fakeUrl"
             |  }
             |}""".stripMargin

        val expected: External = External(external = Url(fakeUrl))

        assert(decode[Icon](json))(isRight(equalTo(expected)))
      },
      test("We should be able to parse a file icon as json") {
        val json: String =
          s"""{
             |  "type": "file",
             |  "file": {
             |     "url": "$fakeUrl",
             |     "expiry_time": "$fakeDatetime"
             |  }
             |}""".stripMargin

        val expected: File = File(file = ExpirableUrl(fakeUrl, fakeDatetime))

        assert(decode[Icon](json))(isRight(equalTo(expected)))
      }
    )
}
