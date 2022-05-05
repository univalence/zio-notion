package zio.notion.model.common

import zio.Scope
import zio.notion.Faker.fakeUUID
import zio.test.{assert, TestEnvironment, ZIOSpecDefault, ZSpec}
import zio.test.Assertion.{equalTo, isRight}

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
