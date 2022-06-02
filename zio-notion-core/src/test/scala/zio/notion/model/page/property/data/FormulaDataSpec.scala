package zio.notion.model.page.property.data

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker.fakeDatetime
import zio.notion.model.page.property.data.FormulaData.{Date, Number}
import zio.test._
import zio.test.Assertion._

object FormulaDataSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("FormulaData serde suite")(
      test("We should be able to parse a string formula as json") {
        val json: String =
          """{
            |    "type": "string",
            |    "string": "string"
            |}""".stripMargin

        val expected = FormulaData.String(string = Some("string"))

        assert(decode[FormulaData](json))(isRight(equalTo(expected)))
      },
      test("We should be able to parse a date formula as json") {
        val json: String =
          s"""{
             |    "type": "date",
             |    "date": {
             |       "start": "$fakeDatetime",
             |       "end": null,
             |       "time_zone": null 
             |    }
             |}""".stripMargin

        val expected: Date = Date(date = Some(DateData(start = fakeDatetime, None, None)))

        assert(decode[FormulaData](json))(isRight(equalTo(expected)))
      },
      test("We should be able to parse a number formula as json") {
        val json: String =
          s"""{
             |    "type": "number",
             |    "number": 10
             |}""".stripMargin

        val expected: Number = Number(number = Some(10d))

        assert(decode[FormulaData](json))(isRight(equalTo(expected)))
      },
      test("We should be able to parse a boolean formula as json") {
        val json: String =
          s"""{
             |    "type": "boolean",
             |    "boolean": true
             |}""".stripMargin

        val expected = FormulaData.Boolean(boolean = Some(true))

        assert(decode[FormulaData](json))(isRight(equalTo(expected)))
      }
    )
}
