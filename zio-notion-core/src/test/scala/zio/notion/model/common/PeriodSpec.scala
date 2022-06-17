package zio.notion.model.common

import io.circe.jawn

import zio._
import zio.notion.Faker.fakeDate
import zio.test._

object PeriodSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Period serde suite")(
      test("We should be able to parse a date as json") {
        val json: String =
          s"""{
             |    "start": "$fakeDate",
             |    "end": null,
             |    "time_zone": null
             |}""".stripMargin

        val expected: Period = Period(start = fakeDate, end = None)

        assertTrue(jawn.decode[Period](json) == Right(expected))
      },
      test("We should be able to stringify a Period") {
        val period: Period = Period(start = fakeDate, end = Some(fakeDate.plusDays(10)))

        assertTrue(period.toString == "2022-12-24 -> 2023-01-03")
      }
    )
}
