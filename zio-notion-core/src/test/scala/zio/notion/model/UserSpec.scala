package zio.notion.model

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.user.{PersonData, User}
import zio.test._
import zio.test.Assertion._

object UserSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("User serde suite")(
      test("We should be able to parse a user payload as json") {
        val json: String =
          s"""{
             |    "object": "user",
             |    "id": "$fakeUUID",
             |    "name": "$fakeName",
             |    "avatar_url": "$fakeUrl",
             |    "type": "person",
             |    "person": {
             |        "email": "$fakeEmail"
             |    }
             |}""".stripMargin

        val expected: User = User.Person(fakeUUID, Some(fakeName), Some(fakeUrl), PersonData(Some(fakeEmail)))

        assert(decode[User](json))(isRight(equalTo(expected)))
      }
    )
}
