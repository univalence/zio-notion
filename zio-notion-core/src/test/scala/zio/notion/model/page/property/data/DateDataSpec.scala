package zio.notion.model.page.property.data

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.page.Property.DateTime
import zio.test._
import zio.test.Assertion._

object DateDataSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("DateData serde suite")(
      test("We should be able to parse a date as json") {
        val json: String =
          s"""{
             |    "start": "$fakeDatetime",
             |    "end": null,
             |    "time_zone": null
             |}""".stripMargin

        val expected: DateTimeData = DateTimeData(start = fakeDatetime, end = None, timeZone = None)

        assert(decode[DateTimeData](json))(isRight(equalTo(expected)))
      }
    )
}
