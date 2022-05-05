package zio.notion.model.page.properties

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker.fakeUUID
import zio.notion.model.common.enumeration.RollupFunction.Count
import zio.notion.model.page.properties.Property.Rollup
import zio.notion.model.page.properties.data.RollupData
import zio.test.{assert, TestEnvironment, ZIOSpecDefault, ZSpec}
import zio.test.Assertion.{equalTo, isRight}

object PropertySpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("RollupData serde suite")(
      test("We should be able to parse a rollup object as json") {
        val json: String =
          s"""{
             |    "type": "rollup",
             |    "id": "$fakeUUID",
             |    "rollup": { 
             |      "type": "number",
             |      "number": 42,
             |      "function": "count" 
             |    }
             |}""".stripMargin

        val expected = Rollup(id = fakeUUID, rollup = RollupData.Number(Some(42d), Count))

        assert(decode[Property](json))(isRight(equalTo(expected)))
      }
    )
}
