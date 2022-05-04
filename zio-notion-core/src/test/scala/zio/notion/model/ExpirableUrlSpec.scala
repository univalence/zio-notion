package zio.notion.model

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.common.TemporaryUrl
import zio.test._
import zio.test.Assertion._

object ExpirableUrlSpec extends ZIOSpecDefault {
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
