package zio.notion.model.page.property.data

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.common
import zio.notion.model.common.Period
import zio.test._
import zio.test.Assertion._

object DateDataSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("DateData serde suite")(
      test("We should be able to parse a date as json") {
        val json: String =
          s"""{
             |    "start": "$fakeDate",
             |    "end": null,
             |    "time_zone": null
             |}""".stripMargin

        val expected: Period = common.Period(start = fakeDate, end = None)

        assert(decode[Period](json))(isRight(equalTo(expected)))
      }
    )
}
