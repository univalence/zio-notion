package zio.notion.model

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker.fakeDate
import zio.notion.model.FormulaData._
import zio.test._
import zio.test.Assertion._

object FormulaDataSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("FormulaData serde suite")(
      test("We should be able to parse a string formula as json") {
        val json: String =
          """{
            |    "type": "string",
            |    "string": "string"
            |}""".stripMargin

        val expected: string = string(string = Some("string"))

        assert(decode[FormulaData](json))(isRight(equalTo(expected)))
      },
      test("We should be able to parse a date formula as json") {
        val json: String =
          s"""{
             |    "type": "date",
             |    "date": {
             |       "start": "$fakeDate",
             |       "end": null,
             |       "time_zone": null 
             |    }
             |}""".stripMargin

        val expected: Date = Date(date = Some(DateData(start = fakeDate, None, None)))

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

        val expected: boolean = boolean(boolean = Some(true))

        assert(decode[FormulaData](json))(isRight(equalTo(expected)))
      }
    )
}
