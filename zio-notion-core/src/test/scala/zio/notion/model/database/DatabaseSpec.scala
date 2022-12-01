package zio.notion.model.database

import io.circe.parser.decode
import io.circe.syntax.EncoderOps

import zio.Scope
import zio.notion.Faker.fakeDatabase
import zio.notion.dsl._
import zio.notion.model.database.Database.Patch
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
            |        
            |        "Status": {
            |            "id": "BuJj",
            |            "name": "StatusForTests",
            |            "type": "status",
            |            "status": {
            |                "options": [
            |                    {
            |                        "id": "xxx",
            |                        "name": "Todo",
            |                        "color": "purple"
            |                    },
            |                    {
            |                        "id": "yyy",
            |                        "name": "Doing",
            |                        "color": "gray"
            |                    },
            |                    {
            |                        "id": "zzz",
            |                        "name": "Done",
            |                        "color": "brown"
            |                    }
            |                ],
            |                "groups": [
            |                    {
            |                        "id": "4919864f-9ecb-49dc-8606-042f206c5adb",
            |                        "name": "To-do",
            |                        "color": "gray",
            |                        "option_ids": [
            |                            "xxx"
            |                        ]
            |                    },
            |                    {
            |                        "id": "30b1987b-48a2-4854-ace0-ffc6eadcebd3",
            |                        "name": "In progress",
            |                        "color": "blue",
            |                        "option_ids": [
            |                            "yyy"
            |                        ]
            |                    },
            |                    {
            |                        "id": "b0b824d3-80c5-4520-957c-959aabf14731",
            |                        "name": "Complete",
            |                        "color": "green",
            |                        "option_ids": [
            |                            "zzz"
            |                        ]
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
      },
      test("We should be able to parse a database json with rich text title") {
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
            |            "type": "mention",
            |            "mention": {
            |                "type": "page",
            |                "page": {
            |                    "id": "46cec14b-98f4-4f2b-b313-5fe3a1a40a88"
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
            |    ],
            |    "properties": {},
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
        val operation = $$"Test".patch.as(date).rename("Date")
        val patch     = Patch.empty.updateOperation(fakeDatabase, operation)

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

        assertTrue(patch.map(_.asJson).map(printer.print) == Right(expected))
      },
      test("We should be able to create a new property description") {
        val operation = $$"New field".create.as(date)
        val patch     = Patch.empty.setOperation(operation)

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
      test("We should be able to remove one existing property") {
        val operation = $$"Test".remove
        val patch     = Patch.empty.setOperation(operation)

        val expected =
          """{
            |  "properties" : {
            |    "Test" : null
            |  }
            |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      },
      test("We should be able to set a title") {
        val operation = setDatabaseTitle("My database")
        val patch     = Patch.empty.setOperation(operation)

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
      },
      test("We should be able to combine operations") {
        val operations = $$"New field".create.as(date) ++ $$"Test".remove
        val patch      = Patch.empty.setOperations(operations)

        val expected =
          """{
            |  "properties" : {
            |    "New field" : {
            |      "date" : {
            |        
            |      }
            |    },
            |    "Test" : null
            |  }
            |}""".stripMargin

        assertTrue(printer.print(patch.asJson) == expected)
      }
    )

}
