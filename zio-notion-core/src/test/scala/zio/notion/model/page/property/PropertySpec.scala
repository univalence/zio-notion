package zio.notion.model.page.property

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.common.Id
import zio.notion.model.common.enumeration.RollupFunction.Count
import zio.notion.model.page.Property
import zio.notion.model.page.Property.{Rollup, _}
import zio.notion.model.page.property.data.RollupData
import zio.test._
import zio.test.Assertion._

object PropertySpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Nothing] =
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
      },
      test("We should be able to parse a relation object as json") {
        val json: String =
          s"""{
             |    "id": "$fakeUUID",
             |    "type": "relation",
             |    "relation": [
             |        {
             |            "id": "$fakeUUID"
             |        }
             |    ]
             |}""".stripMargin

        val expected = Relation(id = fakeUUID, relation = List(Id(fakeUUID)))

        assert(decode[Property](json))(isRight(equalTo(expected)))
      }
    )
}
