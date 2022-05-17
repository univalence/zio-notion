package zio.notion.model.page

import io.circe.parser.decode
import io.circe.syntax.EncoderOps

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.common._
import zio.notion.model.common.Icon.Emoji
import zio.notion.model.page.patch.PatchedProperty._
import zio.notion.model.printer
import zio.test._
import zio.test.Assertion.isRight

object PageSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = serdeSpec + patchSpec

  def serdeSpec: Spec[TestEnvironment with Scope, Any] =
    suite("Page serde suite")(
      test("We should be able to parse a page json") {
        val json: String =
          """{
            |    "object": "page",
            |    "id": "1c2d0a80-3321-4641-9615-f345185de05a",
            |    "created_time": "2022-01-28T10:47:00.000Z",
            |    "last_edited_time": "2022-02-22T08:30:00.000Z",
            |    "created_by": {
            |        "object": "user",
            |        "id": "9774a0c9-ba00-434e-a779-bdc60ace9c71"
            |    },
            |    "last_edited_by": {
            |        "object": "user",
            |        "id": "e20722e7-0439-4122-aa99-8304e561bc1d"
            |    },
            |    "cover": null,
            |    "icon": null,
            |    "parent": {
            |        "type": "database_id",
            |        "database_id": "3868f708-ae46-461f-bfcf-72d34c9536f9"
            |    },
            |    "archived": false,
            |    "properties": {
            |        "Erreurs": {
            |            "id": ">QlB",
            |            "type": "rich_text",
            |            "rich_text": []
            |        },
            |        "Status": {
            |            "id": "@Gcb",
            |            "type": "select",
            |            "select": {
            |                "id": "150fbaa5-7b63-4903-855c-c6973cf0ac48",
            |                "name": "Posted",
            |                "color": "green"
            |            }
            |        },
            |        "Date de publication": {
            |            "id": "DY:@",
            |            "type": "date",
            |            "date": {
            |                "start": "2022-02-22",
            |                "end": null,
            |                "time_zone": null
            |            }
            |        },
            |        "Reviewed by": {
            |            "id": "T[X?",
            |            "type": "people",
            |            "people": []
            |        },
            |        "Mots clés": {
            |            "id": "Xz}B",
            |            "type": "multi_select",
            |            "multi_select": [
            |                {
            |                    "id": "ee7d5ee6-6df1-455d-b0c3-4aee64a0bc63",
            |                    "name": "scala2",
            |                    "color": "brown"
            |                }
            |            ]
            |        },
            |        "Link": {
            |            "id": "jJz@",
            |            "type": "rich_text",
            |            "rich_text": [
            |                {
            |                    "type": "text",
            |                    "text": {
            |                        "content": "https://scastie.scala-lang.org/WLMztXFoQrKQroxsKPVsnQ",
            |                        "link": {
            |                            "url": "https://scastie.scala-lang.org/WLMztXFoQrKQroxsKPVsnQ"
            |                        }
            |                    },
            |                    "annotations": {
            |                        "bold": false,
            |                        "italic": false,
            |                        "strikethrough": false,
            |                        "underline": false,
            |                        "code": false,
            |                        "color": "default"
            |                    },
            |                    "plain_text": "https://scastie.scala-lang.org/WLMztXFoQrKQroxsKPVsnQ",
            |                    "href": "https://scastie.scala-lang.org/WLMztXFoQrKQroxsKPVsnQ"
            |                }
            |            ]
            |        },
            |        "Auteur": {
            |            "id": "pJG_",
            |            "type": "people",
            |            "people": [
            |                {
            |                    "object": "user",
            |                    "id": "9774a0c9-ba00-434e-a779-bdc60ace9c71",
            |                    "name": "Dylan DO AMARAL",
            |                    "avatar_url": "https://lh3.googleusercontent.com/-6B9dSBYVuvk/AAAAAAAAAAI/AAAAAAAAAAc/su4if5KvrbA/photo.jpg",
            |                    "type": "person",
            |                    "person": {}
            |                }
            |            ]
            |        },
            |        "Type": {
            |            "id": "|=~Q",
            |            "type": "select",
            |            "select": {
            |                "id": "01f5896b-8c7c-4c6c-a76f-c1c2eb6d12c9",
            |                "name": "Tips",
            |                "color": "purple"
            |            }
            |        },
            |        "Name": {
            |            "id": "title",
            |            "type": "title",
            |            "title": [
            |                {
            |                    "type": "text",
            |                    "text": {
            |                        "content": "Les énumérations en Scala 2.X",
            |                        "link": null
            |                    },
            |                    "annotations": {
            |                        "bold": false,
            |                        "italic": false,
            |                        "strikethrough": false,
            |                        "underline": false,
            |                        "code": false,
            |                        "color": "default"
            |                    },
            |                    "plain_text": "Les énumérations en Scala 2.X",
            |                    "href": null
            |                }
            |            ]
            |        }
            |    },
            |    "url": "https://www.notion.so/Les-num-rations-en-Scala-2-X-1c2d0a80332146419615f345185de05a"
            |}""".stripMargin

        assert(decode[Page](json))(isRight)
      },
      test("We should be able to encode an empty page patch") {
        val patch = Page.Patch(fakePage)

        val expected =
          """{
            |  
            |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      }
    )

  def patchSpec: Spec[TestEnvironment with Scope, Any] =
    suite("Page update suite")(
      test("We should be able to update one property") {
        val maybePatch = fakePage.patch.updateProperty(PatchedCheckbox.check.on("Checkbox"))

        val expected =
          """{
            |  "properties" : {
            |    "Checkbox" : {
            |      "checkbox" : true
            |    }
            |  }
            |}""".stripMargin

        assertTrue(maybePatch.map(patch => printer.print(patch.asJson)) == Right(expected))
      },
      test("We should be able to remove one property") {
        val patch = fakePage.patch.removeProperty("Checkbox")

        val expected =
          """{
            |  "properties" : {
            |    "Checkbox" : null
            |  }
            |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      },
      test("We should be able to remove an icon") {
        val patch = fakePage.patch.removeIcon

        val expected =
          """{
            |  "icon" : null
            |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      },
      test("We should be able to update an icon") {
        val patch = fakePage.patch.updateIcon(Emoji(fakeEmoji))

        val expected =
          s"""{
             |  "icon" : {
             |    "emoji" : "$fakeEmoji",
             |    "type" : "emoji"
             |  }
             |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      },
      test("We should be able to remove a cover") {
        val patch = fakePage.patch.removeCover

        val expected =
          """{
            |  "cover" : null
            |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      },
      test("We should be able to update a cover") {
        val patch = fakePage.patch.updateCover(Cover.External(Url(fakeUrl)))

        val expected =
          s"""{
             |  "cover" : {
             |    "external" : {
             |      "url" : "$fakeUrl"
             |    },
             |    "type" : "external"
             |  }
             |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      },
      test("We should be able to archive a page") {
        val patch = fakePage.patch.unarchive

        val expected =
          """{
            |  "archived" : false
            |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      },
      test("We should be able to chain patches") {
        val patch = fakePage.patch.unarchive.removeIcon

        val expected =
          """{
            |  "archived" : false,
            |  "icon" : null
            |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      },
      test("Updating a property twice should apply the second update on the first one") {
        val maybePatch =
          for {
            p1 <- fakePage.patch.updateProperty(PatchedCheckbox.check.on("Checkbox"))
            p2 <- p1.updateProperty(PatchedCheckbox.reverse.onAll)
          } yield p2

        val expected =
          """{
            |  "properties" : {
            |    "Checkbox" : {
            |      "checkbox" : false
            |    }
            |  }
            |}""".stripMargin

        assertTrue(maybePatch.map(patch => printer.print(patch.asJson)) == Right(expected))
      }
    )
}
