package zio.notion.model

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.page.properties.data
import zio.notion.model.page.properties.data.DateData
import zio.test._
import zio.test.Assertion._

object DateDataSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("DateData serde suite")(
      test("We should be able to parse a date as json") {
        val json: String =
          s"""{
             |    "start": "$fakeDate",
             |    "end": null,
             |    "time_zone": null
             |}""".stripMargin

        val expected: DateData = data.DateData(start = fakeDate, end = None, timeZone = None)

        assert(decode[DateData](json))(isRight(equalTo(expected)))
      }
    )
}
