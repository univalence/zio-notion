package zio.notion

import zio._
import zio.notion.Faker._
import zio.notion.NotionClient.NotionResponse
import zio.notion.model.common.{Cover, Icon}
import zio.notion.model.common.richtext.RichTextData
import zio.notion.model.database.{Database, PropertyDefinitionPatch}
import zio.notion.model.database.query.Query
import zio.notion.model.page.Page

/** Notion client mock for test purpose */
final case class TestNotionClient() extends NotionClient {
  def pagePayload(pageId: String): String =
    s"""{
       |    "object": "page",
       |    "id": "$pageId",
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

  override def retrievePage(pageId: String): IO[NotionError, NotionResponse] = ZIO.succeed(pagePayload(pageId))

  override def retrieveDatabase(databaseId: String): IO[NotionError, NotionResponse] =
    ZIO.succeed(s"""{
                   |    "object": "database",
                   |    "id": "$databaseId",
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
                   |            "Property 7": {
                   |            "id": "%3BGTP",
                   |            "name": "Property 7",
                   |            "type": "email",
                   |            "email": {}
                   |        },
                   |            "Property": {
                   |            "id": "%3Cqli",
                   |            "name": "Property",
                   |            "type": "number",
                   |            "number": {
                   |                "format": "number"
                   |            }
                   |        },
                   |            "Property 3": {
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
                   |        "people": {
                   |            "id": "wWmU",
                   |            "name": "people",
                   |            "type": "people",
                   |            "people": {}
                   |        },
                   |                "Property 1": {
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
                   |                "Property 9": {
                   |            "id": "g~Ph",
                   |            "name": "Property 9",
                   |            "type": "formula",
                   |            "formula": {
                   |                "expression": "prop(\\"Property\\") + 1"
                   |            }
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
                   |}""".stripMargin)

  override def retrieveUser(userId: String): IO[NotionError, NotionResponse] =
    ZIO.succeed(s"""{
                   |    "object": "user",
                   |    "id": "$userId",
                   |    "name": "$fakeName",
                   |    "avatar_url": "$fakeUrl",
                   |    "type": "person",
                   |    "person": {
                   |        "email": "$fakeEmail"
                   |    }
                   |}""".stripMargin)

  override def queryDatabase(databaseId: String, query: Query): IO[NotionError, NotionResponse] = ZIO.succeed("TODO")

  override def updatePage(patch: Page.Patch): IO[NotionError, NotionResponse] = retrievePage(patch.page.id)

  override def updateDatabase(patch: Database.Patch): IO[NotionError, NotionResponse] = retrieveDatabase(patch.database.id)

  override def createDatabase(
      pageId: String,
      title: Seq[RichTextData],
      icon: Option[Icon],
      cover: Option[Cover],
      properties: Map[String, PropertyDefinitionPatch.PropertySchema]
  ): IO[NotionError, NotionResponse] = retrieveDatabase(fakeUUID)
}

object TestNotionClient {
  val layer: ULayer[NotionClient] = ZLayer.succeed(TestNotionClient())
}
