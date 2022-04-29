package zio.notion.model

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.Cover._
import zio.test._
import zio.test.Assertion._

object CoverSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("Cover serde suite")(
      test("We should be able to parse an external cover as json") {
        val json: String =
          s"""{
             |  "type": "external",
             |  "external": {
             |     "url": "$fakeUrl"
             |  }
             |}""".stripMargin

        val expected: External = External(external = Url(fakeUrl))

        assert(decode[Cover](json))(isRight(equalTo(expected)))
      },
      test("We should be able to parse a file cover as json") {
        val json: String =
          s"""{
             |  "type": "file",
             |  "file": {
             |     "url": "$fakeUrl",
             |     "expiry_time": "$fakeDatetime"
             |  }
             |}""".stripMargin

        val expected: File = File(file = ExpirableUrl(fakeUrl, fakeDatetime))

        assert(decode[Cover](json))(isRight(equalTo(expected)))
      }
    )
}
