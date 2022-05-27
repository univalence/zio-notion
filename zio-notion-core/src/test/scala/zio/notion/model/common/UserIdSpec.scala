package zio.notion.model.common

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker.fakeUUID
import zio.test.{assert, Spec, TestEnvironment, ZIOSpecDefault}
import zio.test.Assertion.{equalTo, isRight}

object UserIdSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("UserId serde suite")(
      test("We should be able to parse a user id as json") {
        val json: String =
          s"""{
             |    "object": "user",
             |    "id": "$fakeUUID"
             |}""".stripMargin

        val expected: Id = Id(fakeUUID)

        assert(decode[Id](json))(isRight(equalTo(expected)))
      }
    )
}
