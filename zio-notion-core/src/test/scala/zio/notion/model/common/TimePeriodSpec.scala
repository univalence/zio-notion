package zio.notion.model.common

import io.circe.jawn

import zio.Scope
import zio.notion.Faker.fakeDatetime
import zio.notion.model.common
import zio.test._

object TimePeriodSpec extends ZIOSpecDefault {

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

        assertTrue(jawn.decode[TimePeriod](json) == Right(expected))
      },
      test("We should be able to stringify a Period") {
        val period: TimePeriod = TimePeriod(start = fakeDatetime, end = Some(fakeDatetime.plusDays(10)), None)

        assertTrue(period.toString == "2022-12-24T15:10Z -> 2023-01-03T15:10Z")
      }
    )
}
