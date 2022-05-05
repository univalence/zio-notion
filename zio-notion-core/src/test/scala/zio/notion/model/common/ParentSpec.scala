package zio.notion.model.common

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker.fakeUUID
import zio.notion.model.common.Parent.{DatabaseId, PageId, Workspace}
import zio.test.{assert, TestEnvironment, ZIOSpecDefault, ZSpec}
import zio.test.Assertion.{equalTo, isRight}

object ParentSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("Parent serde suite")(
      test("We should be able to parse a page parent json") {
        val raw: String =
          s"""{
             |   "type": "page_id",
             |   "page_id":"$fakeUUID"
             |}""".stripMargin

        val expected = PageId(pageId = fakeUUID)

        assert(decode[Parent](raw))(isRight(equalTo(expected)))
      },
      test("We should be able to parse a database parent json") {
        val raw: String =
          s"""{
             |   "type": "database_id",
             |   "database_id": "$fakeUUID"
             |}""".stripMargin

        val expected = DatabaseId(fakeUUID)

        assert(decode[Parent](raw))(isRight(equalTo(expected)))
      },
      test("We should be able to parse a workspace parent json") {
        val raw: String =
          """{
            |   "type": "workspace"
            |}""".stripMargin

        val expected = Workspace

        assert(decode[Parent](raw))(isRight(equalTo(expected)))
      }
    )
}
