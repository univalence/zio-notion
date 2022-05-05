package zio.notion.model.common

import zio.Scope
import zio.notion.Faker.{fakeDatetime, fakeUrl}
import zio.notion.model.common.Cover.{External, File}
import zio.test.{assert, TestEnvironment, ZIOSpecDefault, ZSpec}
import zio.test.Assertion.{equalTo, isRight}

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

        val expected: File = File(file = TemporaryUrl(fakeUrl, fakeDatetime))

        assert(decode[Cover](json))(isRight(equalTo(expected)))
      }
    )
}
