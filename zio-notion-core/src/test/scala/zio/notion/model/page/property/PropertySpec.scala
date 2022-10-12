package zio.notion.model.page.property

import io.circe.parser.decode

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.common
import zio.notion.model.common.{richtext, Id, Period, TimePeriod}
import zio.notion.model.common.enumeration.Color.Yellow
import zio.notion.model.common.enumeration.RollupFunction.Count
import zio.notion.model.common.richtext.RichTextFragment
import zio.notion.model.common.richtext.RichTextFragment.Mention.MentionData
import zio.notion.model.page.Property
import zio.notion.model.page.Property._
import zio.notion.model.page.property.data.{RollupData, SelectData}
import zio.test._
import zio.test.Assertion._

import java.time.ZoneOffset

object PropertySpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Nothing] =
    suite("Property serde suite")(
      test("we should be able to parse a null status property") {
        val json: String =
          s"""{
             |    "id": "BuJj",
             |    "type": "status",
             |    "status": null
             |}""".stripMargin

        val expected = Status("BuJj", None)

        assert(decode[Property](json))(isRight(equalTo(expected)))
      },
      test("we should be able to parse a status property") {
        val json: String =
          s"""{
             |    "id": "BuJj",
             |    "type": "status",
             |    "status": {
             |        "id": "xxx",
             |        "name": "Doing",
             |        "color": "yellow"
             |    }
             |}""".stripMargin

        val expected = Status("BuJj", Some(SelectData("xxx", "Doing", Yellow)))

        assert(decode[Property](json))(isRight(equalTo(expected)))
      },
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
      },
      test("We should be able to parse a relation object as json") {
        val json: String =
          s"""{
             |    "id": "$fakeUUID",
             |    "type": "relation",
             |    "relation": [
             |        {
             |            "id": "$fakeUUID"
             |        }
             |    ]
             |}""".stripMargin

        val expected = Relation(id = fakeUUID, relation = List(Id(fakeUUID)))

        assert(decode[Property](json))(isRight(equalTo(expected)))
      },
      test("We should be able to parse a date object as json containing date") {
        val json: String =
          s"""{
             |    "id": "$fakeUUID",
             |    "type": "date",
             |    "date": {
             |        "start": "2022-12-24",
             |        "end": null,
             |        "time_zone": null
             |    }
             |}""".stripMargin

        val expected =
          Date(
            id   = fakeUUID,
            date = Some(Period(fakeDate, None))
          )

        assert(decode[Property](json))(isRight(equalTo(expected)))
      },
      test("We should be able to parse a date object as json containing datetime") {
        val json: String =
          s"""{
             |    "id": "$fakeUUID",
             |    "type": "date",
             |    "date": {
             |        "start": "2022-12-24T17:10+02:00",
             |        "end": null,
             |        "time_zone": null
             |    }
             |}""".stripMargin

        val expected =
          DateTime(
            id   = fakeUUID,
            date = Some(TimePeriod(fakeDatetime.withOffsetSameInstant(ZoneOffset.ofHours(2)), None, None))
          )

        assert(decode[Property](json))(isRight(equalTo(expected)))
      },
      test("We should be able to convert a datetime property into a date property") {
        val source =
          DateTime(
            id   = fakeUUID,
            date = Some(common.TimePeriod(fakeDatetime.withOffsetSameInstant(ZoneOffset.ofHours(2)), None, None))
          )

        val expected =
          Date(
            id   = fakeUUID,
            date = Some(common.Period(fakeDate, None))
          )

        assertTrue(source.toDateProperty == expected)
      },
      test("We should be able to parse a title object as json containing mention") {
        val json: String =
          s"""{
             |    "id": "$fakeUUID",
             |    "type": "title",
             |    "title": [
             |        {
             |            "type": "mention",
             |            "mention": {
             |                "type": "date",
             |                "date": {
             |                    "start": "2022-12-24T15:10Z",
             |                    "end": null,
             |                    "time_zone": null
             |                }
             |            },
             |            "annotations": {
             |                "bold": false,
             |                "italic": false,
             |                "strikethrough": false,
             |                "underline": false,
             |                "code": false,
             |                "color": "default"
             |            },
             |            "plain_text": "Untitled",
             |            "href": "https://www.notion.so/46cec14b98f44f2bb3135fe3a1a40a88"
             |        }
             |    ]
             |}""".stripMargin

        val expected =
          Title(
            id = fakeUUID,
            title =
              List(
                RichTextFragment.Mention(
                  mention     = MentionData.DateTime(common.TimePeriod(fakeDatetime, None, None)),
                  annotations = richtext.Annotations.default,
                  plainText   = "Untitled",
                  href        = Some("https://www.notion.so/46cec14b98f44f2bb3135fe3a1a40a88")
                )
              )
          )

        assert(decode[Property](json))(isRight(equalTo(expected)))
      }
    )
}
