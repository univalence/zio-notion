package zio.notion.model.user

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.test._

object UserSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("User serde suite")(
      test("We should be able to parse a hidden user payload as json") {
        val json: String =
          s"""{
             |    "object": "user",
             |    "id": "$fakeUUID"
             |}""".stripMargin

        val expected: User = User.Hidden(fakeUUID)

        assertTrue(decode[User](json) == Right(expected))
      },
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

        val expected: User = User.Person(fakeUUID, Some(fakeName), Some(fakeUrl), User.Person.Data(Some(fakeEmail)))

        assertTrue(decode[User](json) == Right(expected))
      },
      test("We should be able to parse a bot payload as json without bot data") {
        val json: String =
          s"""{
             |    "object": "user",
             |    "id": "$fakeUUID",
             |    "name": "$fakeName",
             |    "avatar_url": "$fakeUrl",
             |    "type": "bot",
             |    "bot": {}
             |}""".stripMargin

        val expected: User = User.Bot(fakeUUID, Some(fakeName), Some(fakeUrl), None)

        assertTrue(decode[User](json) == Right(expected))
      },
      test("We should be able to parse a bot payload as json with bot data") {
        val json: String =
          s"""{
             |    "object": "user",
             |    "id": "$fakeUUID",
             |    "name": "$fakeName",
             |    "avatar_url": "$fakeUrl",
             |    "type": "bot",
             |    "bot": {
             |      "owner": {
             |        "id": "$fakeUUID",
             |        "name": "$fakeName",
             |        "avatar_url": "$fakeUrl",
             |        "type": "person",
             |        "person": {
             |            "email": "$fakeEmail"
             |        }
             |      }
             |    }
             |}""".stripMargin

        val owner: Owner   = Owner.Person(fakeUUID, Some(fakeName), Some(fakeUrl), Some(Owner.Person.Data(fakeEmail)))
        val expected: User = User.Bot(fakeUUID, Some(fakeName), Some(fakeUrl), Some(User.Bot.Data(owner)))

        assertTrue(decode[User](json) == Right(expected))
      }
    )
}
