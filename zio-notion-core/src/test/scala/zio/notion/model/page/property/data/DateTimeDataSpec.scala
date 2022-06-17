package zio.notion.model.page.property.data

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.common
import zio.notion.model.common.TimePeriod
import zio.test._
import zio.test.Assertion._

object DateTimeDataSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("DateTimeData serde suite")(
      test("We should be able to parse a datetime as json") {
        val json: String =
          s"""{
             |    "start": "$fakeDatetime",
             |    "end": null,
             |    "time_zone": null
             |}""".stripMargin

        val expected: TimePeriod = common.TimePeriod(start = fakeDatetime, end = None, timeZone = None)

        assert(decode[TimePeriod](json))(isRight(equalTo(expected)))
      }
    )
}
