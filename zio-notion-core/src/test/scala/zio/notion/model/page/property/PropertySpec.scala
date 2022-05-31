package zio.notion.model.page.property

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.common.Id
import zio.notion.model.common.enumeration.RollupFunction.Count
import zio.notion.model.page.Property
import zio.notion.model.page.Property._
import zio.notion.model.page.property.data.{DateData, RollupData}
import zio.test._
import zio.test.Assertion._

import java.time.ZoneId

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
      },
      test("We should be able to parse a date object as json containing datetime") {
        val json: String =
          s"""{
             |    "id": "$fakeUUID",
             |    "type": "date",
             |    "date": {
             |        "start": "2022-02-22T00:00:00.000+02:00",
             |        "end": null,
             |        "time_zone": null
             |    }
             |}""".stripMargin

        val expected =
          Date(
            id = fakeUUID,
            date =
              Some(
                DateData(
                  fakeZonedDateTime.withZoneSameLocal(ZoneId.of("+02:00")),
                  None,
                  None
                )
              )
          )

        assert(decode[Property](json))(isRight(equalTo(expected)))
      }
    )
}
