package zio.notion.model

import io.circe
import io.circe.parser.decode
import zio.Scope
import zio.test._
import zio.test.Assertion.isRight

object DatabaseSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("DB serde suite")(
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
            |        "Property 7": {
            |            "id": "%3BGTP",
            |            "name": "Property 7",
            |            "type": "email",
            |            "email": {}
            |        },
            |        "Property": {
            |            "id": "%3Cqli",
            |            "name": "Property",
            |            "type": "number",
            |            "number": {
            |                "format": "number"
            |            }
            |        },
            |        "Property 2": {
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
            |        "Property 8": {
            |            "id": "JYsh",
            |            "name": "Property 8",
            |            "type": "phone_number",
            |            "phone_number": {}
            |        },
            |        "Property 1": {
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
            |        "Property 3": {
            |            "id": "M%3E%3B%5B",
            |            "name": "Property 3",
            |            "type": "date",
            |            "date": {}
            |        },
            |        "Property 5": {
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
            |        "Property 9": {
            |            "id": "g~Ph",
            |            "name": "Property 9",
            |            "type": "formula",
            |            "formula": {
            |                "expression": "prop(\"Property\") + 1"
            |            }
            |        },
            |        "people": {
            |            "id": "wWmU",
            |            "name": "people",
            |            "type": "people",
            |            "people": {}
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
}