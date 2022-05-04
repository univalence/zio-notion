package zio.notion.model
import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.common.enums.Color.Green
import zio.notion.model.page.properties.data
import zio.notion.model.page.properties.data.SelectData
import zio.test._
import zio.test.Assertion._

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
