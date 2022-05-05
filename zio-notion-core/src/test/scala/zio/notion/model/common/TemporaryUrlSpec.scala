package zio.notion.model.common

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker.{fakeDatetime, fakeUrl}
import zio.notion.model.common
import zio.test.{assert, TestEnvironment, ZIOSpecDefault, ZSpec}
import zio.test.Assertion.{equalTo, isRight}

object TemporaryUrlSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("ExpirableUrl serde suite")(
      test("We should be able to parse an expriable url as json") {
        val json: String =
          s"""{
             |    "url": "$fakeUrl",
             |    "expiry_time": "$fakeDatetime"
             |}""".stripMargin

        val expected: TemporaryUrl = common.TemporaryUrl(url = fakeUrl, expiryTime = fakeDatetime)

        assert(decode[TemporaryUrl](json))(isRight(equalTo(expected)))
      }
    )
}
