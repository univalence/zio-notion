package zio.notion.model.page.property.data

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker.fakeLocalDate
import zio.test.{assert, TestEnvironment, ZIOSpecDefault, ZSpec}
import zio.test.Assertion.{equalTo, isRight}

object DateDataSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("DateData serde suite")(
      test("We should be able to parse a date as json") {
        val json: String =
          s"""{
             |    "start": "$fakeLocalDate",
             |    "end": null,
             |    "time_zone": null
             |}""".stripMargin

        val expected: DateData = DateData(start = fakeLocalDate, end = None, timeZone = None)

        assert(decode[DateData](json))(isRight(equalTo(expected)))
      }
    )
}
