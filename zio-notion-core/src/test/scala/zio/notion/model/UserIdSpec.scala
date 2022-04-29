package zio.notion.model

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.test._
import zio.test.Assertion._

object UserIdSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("UserId serde suite")(
      test("We should be able to parse a user id as json") {
        val json: String =
          s"""{
             |    "object": "user",
             |    "id": "$fakeUUID"
             |}""".stripMargin

        val expected: UserId = UserId(fakeUUID)

        assert(decode[UserId](json))(isRight(equalTo(expected)))
      }
    )
}
