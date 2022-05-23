package zio.notion.model.database

import io.circe.parser.decode
import io.circe.syntax.EncoderOps

import zio.Scope
import zio.notion.Faker.{fakeDatabase, fakeUUID}
import zio.notion.NotionError.PropertyNotExist
import zio.notion.dsl._
import zio.notion.dsl.DatabaseUpdateDSL._
import zio.notion.model.printer
import zio.test.{assert, assertTrue, Spec, TestEnvironment, ZIOSpecDefault}
import zio.test.Assertion.isRight

object DatabaseSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = serdeSpec + patchSpec

  def serdeSpec: Spec[TestEnvironment with Scope, Any] =
    suite("Database serde suite")(
      test("We should be able to parse a database json") {
        val json: String =
          """{
            |    "object": "database",
            |    "id": "7caa81b5-6383-4270-b89c-c386a9f0de13",
            |    "cover": null,
            |    "icon": null,
            |    "created_time": "2022-03-28T07:05:00.000Z",
            |    "created_by": {
            |        "object": "user",
            |        "id": "99907f33-df93-4a31-91a9-c662d7437cdd"
            |    },
            |    "last_edited_by": {
            |        "object": "user",
            |        "id": "99907f33-df93-4a31-91a9-c662d7437cdd"
            |    },
            |    "last_edited_time": "2022-05-03T13:35:00.000Z",
            |    "title": [
            |        {
            |            "type": "text",
            |            "text": {
            |                "content": "Pages",
            |                "link": null
            |            },
            |            "annotations": {
            |                "bold": false,
            |                "italic": false,
            |                "strikethrough": false,
            |                "underline": false,
            |                "code": false,
            |                "color": "default"
            |            },
            |            "plain_text": "Pages",
            |            "href": null
            |        }
            |    ],
            |    "properties": {
            |            "Property 9": {
            |            "id": "g~Ph",
            |            "name": "Property 9",
            |            "type": "formula",
            |            "formula": {
            |                "expression": "prop(\"Property\") + 1"
            |            }
            |        },
            |            "Property 5": {
            |            "id": "RjnZ",
            |            "name": "Property 5",
            |            "type": "checkbox",
            |            "checkbox": {}
            |        },
            |        "Property 4": {
            |            "id": "%5B%5CE_",
            |            "name": "Property 4",
            |            "type": "files",
            |            "files": {}
            |        },
            |        "Property 6": {
            |            "id": "d%5EEf",
            |            "name": "Property 6",
            |            "type": "url",
            |            "url": {}
            |        },
            |            "Property 3": {
            |            "id": "M%3E%3B%5B",
            |            "name": "Property 3",
            |            "type": "date",
            |            "date": {}
            |        },
            |            "Property 1": {
            |            "id": "KwBT",
            |            "name": "Property 1",
            |            "type": "select",
            |            "select": {
            |                "options": [
            |                    {
            |                        "id": "e3600dbb-f3c2-47d3-8928-d59519c637af",
            |                        "name": "single select",
            |                        "color": "green"
            |                    }
            |                ]
            |            }
            |        },
            |            "Property 8": {
            |            "id": "JYsh",
            |            "name": "Property 8",
            |            "type": "phone_number",
            |            "phone_number": {}
            |        },
            |            "Property 2": {
            |            "id": "CUj~",
            |            "name": "Property 2",
            |            "type": "multi_select",
            |            "multi_select": {
            |                "options": [
            |                    {
            |                        "id": "ca82480b-5dae-444a-bc7b-d7b1560315de",
            |                        "name": "multi",
            |                        "color": "red"
            |                    },
            |                    {
            |                        "id": "da0143a2-d549-47d2-9de5-2787b0fd3607",
            |                        "name": "select",
            |                        "color": "brown"
            |                    }
            |                ]
            |            }
            |        },
            |        "Property": {
            |            "id": "%3Cqli",
            |            "name": "Property",
            |            "type": "number",
            |            "number": {
            |                "format": "number"
            |            }
            |        },
            |        "Property 7": {
            |            "id": "%3BGTP",
            |            "name": "Property 7",
            |            "type": "email",
            |            "email": {}
            |        },
            |        "people": {
            |            "id": "wWmU",
            |            "name": "people",
            |            "type": "people",
            |            "people": {}
            |        },
            |        "Property 11": {
            |            "id": "IB%7Dx",
            |            "name": "Property 11",
            |            "type": "rollup",
            |            "rollup": {
            |                "rollup_property_name": "Name",
            |                "relation_property_name": "Property 10",
            |                "rollup_property_id": "title",
            |                "relation_property_id": "Krck",
            |                "function": "count"
            |            }
            |        },
            |        "Property 10": {
            |            "id": "Krck",
            |            "name": "Property 10",
            |            "type": "relation",
            |            "relation": {
            |                "database_id": "678cf788-8470-4798-8336-a285836c9de6",
            |                "synced_property_name": "Related to Pages (Property 10)",
            |                "synced_property_id": "D;@M"
            |            }
            |        },
            |        "name": {
            |            "id": "title",
            |            "name": "name",
            |            "type": "title",
            |            "title": {}
            |        }
            |    },
            |    "parent": {
            |        "type": "page_id",
            |        "page_id": "a43aaa9d-601d-4d74-802a-f5681db935e1"
            |    },
            |    "url": "https://www.notion.so/7caa81b563834270b89cc386a9f0de13",
            |    "archived": false
            |}""".stripMargin

        assert(decode[Database](json))(isRight)
      }
    )

  def patchSpec: Spec[TestEnvironment with Scope, Any] =
    suite("Database update suite")(
      test("We should be able to update one property") {
        val patch = fakeDatabase.patch.updateProperty($$"Test".patch.as(date).rename("Date"))

        val expected =
          """{
            |  "properties" : {
            |    "Test" : {
            |      "name" : "Date",
            |      "date" : {
            |        
            |      }
            |    }
            |  }
            |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      },
      test("We should be able to create a new property description") {
        val patch = fakeDatabase.patch.updateProperty($$"New field".patch.as(date))

        val expected =
          """{
            |  "properties" : {
            |    "New field" : {
            |      "date" : {
            |        
            |      }
            |    }
            |  }
            |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      },
      test("We should be able to rename a newly created property description (if we rename on create, we have to use the rename value)") {
        val patch = fakeDatabase.patch.updateProperty($$"New field".patch.as(date).rename("Date"))

        val expected =
          """{
            |  "properties" : {
            |    "Date" : {
            |      "date" : {
            |        
            |      }
            |    }
            |  }
            |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      },
      test("We should be able to remove one existing property") {
        val maybePatch = fakeDatabase.patch.removeProperty("Test")

        val expected =
          """{
            |  "properties" : {
            |    "Test" : null
            |  }
            |}""".stripMargin

        assertTrue(maybePatch.map(patch => printer.print(patch.asJson)) == Right(expected))
      },
      test("We should return an error when we remove one non existing property") {
        val maybePatch = fakeDatabase.patch.removeProperty("Void")

        assertTrue(maybePatch.map(patch => printer.print(patch.asJson)) == Left(PropertyNotExist("Void", fakeUUID)))
      },
      test("We should be able to update the title") {
        val patch = fakeDatabase.patch.rename("My database")

        val expected =
          """{
            |  "title" : [
            |    {
            |      "text" : {
            |        "content" : "My database",
            |        "link" : null
            |      },
            |      "annotations" : {
            |        "bold" : false,
            |        "italic" : false,
            |        "strikethrough" : false,
            |        "underline" : false,
            |        "code" : false,
            |        "color" : "default"
            |      },
            |      "plain_text" : "My database",
            |      "href" : null,
            |      "type" : "text"
            |    }
            |  ]
            |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      }
    )

}
