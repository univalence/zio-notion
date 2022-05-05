package zio.notion.model.page.properties.data

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker.{fakeName, fakeUUID}
import zio.notion.model.common.enumeration.Color.Green
import zio.notion.model.page.properties.data
import zio.test.{assert, TestEnvironment, ZIOSpecDefault, ZSpec}
import zio.test.Assertion.{equalTo, isRight}

object SelectDataSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("SelectData serde suite")(
      test("We should be able to parse a select as json") {
        val json: String =
          s"""{
             |    "id": "$fakeUUID",
             |    "name": "$fakeName",
             |    "color": "green"
             |}""".stripMargin

        val expected: SelectData = data.SelectData(id = fakeUUID, name = fakeName, color = Green)

        assert(decode[SelectData](json))(isRight(equalTo(expected)))
      }
    )
}
