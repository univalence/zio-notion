package zio.notion.model

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.notion.Faker.FakeProperty._
import zio.notion.model.Property._
import zio.notion.model.RollupFunction.Count
import zio.test._
import zio.test.Assertion._

object PropertySpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] = serdeSpec + capabilitySpec

  def serdeSpec: Spec[Any, TestFailure[Nothing], TestSuccess] =
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
      }
    )

  def capabilitySpec: Spec[Any, TestFailure[Nothing], TestSuccess] =
    suite("Property capability suite")(
      test("Title property can be renamed") {
        Title
          .rename("newTitle")
          .transform(fakeTitle)
          .map(title => assertTrue(title.title.headOption.map(_.asInstanceOf[RichTextData.Text].plainText).contains("newTitle")))
      },
      test("Date property can be started now") {
        Date.now
          .transform(fakeDate)
          .map(date => assertTrue(date.date.map(_.start.toString).contains("1970-01-01")))
      },
      test("Date property can be between two dates") {
        Date
          .between(fakeLocalDate, fakeLocalDate.plusDays(1))
          .transform(fakeDate)
          .map(date => assertTrue(date.date.map(_.start.toString).contains("2022-02-22") && date.date.flatMap(_.end.map(_.toString)).contains("2022-02-23")))
      }
    )

}
