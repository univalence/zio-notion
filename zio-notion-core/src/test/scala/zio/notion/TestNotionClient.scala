package zio.notion

import zio._
import zio.notion.Faker._
import zio.notion.NotionClient.NotionResponse
import zio.notion.model.block.BlockContent
import zio.notion.model.common.{Cover, Icon, Parent}
import zio.notion.model.common.Parent.PageId
import zio.notion.model.common.richtext.RichTextFragment
import zio.notion.model.database.{Database, PatchedPropertyDefinition}
import zio.notion.model.database.query.Query
import zio.notion.model.page.{Page, PatchedProperty}

/** Notion client mock for test purpose */
final case class TestNotionClient() extends NotionClient {

  private def pagePayload(pageId: String): String =
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

  private def blockPayload(blockId: String): String =
    s"""{
       |    "object": "block",
       |    "id": "$blockId",
       |    "created_time": "2022-06-22T08:03:00.000Z",
       |    "last_edited_time": "2022-06-22T08:06:00.000Z",
       |    "created_by": {
       |        "object": "user",
       |        "id": "9774a0c9-ba00-434e-a779-bdc60ace9c71"
       |    },
       |    "last_edited_by": {
       |        "object": "user",
       |        "id": "9774a0c9-ba00-434e-a779-bdc60ace9c71"
       |    },
       |    "has_children": true,
       |    "archived": false,
       |    "type": "paragraph",
       |    "paragraph": {
       |        "color": "default",
       |        "rich_text": [
       |            {
       |                "type": "text",
       |                "text": {
       |                    "content": "qdsqdsds",
       |                    "link": null
       |                },
       |                "annotations": {
       |                    "bold": false,
       |                    "italic": false,
       |                    "strikethrough": false,
       |                    "underline": false,
       |                    "code": false,
       |                    "color": "default"
       |                },
       |                "plain_text": "qdsqdsds",
       |                "href": null
       |            }
       |        ]
       |    }
       |}""".stripMargin
  override def retrievePage(pageId: String)(implicit trace: Trace): IO[NotionError, NotionResponse] = ZIO.succeed(pagePayload(pageId))

  override def retrieveDatabase(databaseId: String)(implicit trace: Trace): IO[NotionError, NotionResponse] =
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

  override def retrieveUser(userId: String)(implicit trace: Trace): IO[NotionError, NotionResponse] =
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

  override def retrieveUsers(pagination: Pagination)(implicit trace: Trace): IO[NotionError, NotionResponse] =
    ZIO.succeed(
      pagination.startCursor match {
        case Some("a") =>
          s"""{
             |  "results": [
             |    {
             |      "object": "user",
             |      "id": "9a3b5ae0-c6e6-482d-b0e1-ed315ee6dc57",
             |      "type": "bot",
             |      "bot": {},
             |      "name": "Doug Engelbot",
             |      "avatar_url": "https://secure.notion-static.com/6720d746-3402-4171-8ebb-28d15144923c.jpg"
             |    }
             |  ],
             |  "next_cursor": null,
             |  "has_more": true
             |}""".stripMargin
        case _ =>
          s"""{
             |  "results": [
             |    {
             |      "object": "user",
             |      "id": "d40e767c-d7af-4b18-a86d-55c61f1e39a4",
             |      "type": "person",
             |      "person": {
             |        "email": "avo@example.org"
             |      },
             |      "name": "Avocado Lovelace",
             |      "avatar_url": "https://secure.notion-static.com/e6a352a8-8381-44d0-a1dc-9ed80e62b53d.jpg"
             |    }
             |  ],
             |  "next_cursor": "a",
             |  "has_more": true
             |}""".stripMargin

      }
    )

  override def retrieveBlock(blockId: String)(implicit trace: Trace): IO[NotionError, NotionResponse] = ZIO.succeed(blockPayload(fakeUUID))

  override def retrieveBlocks(pageId: String, pagination: Pagination)(implicit trace: Trace): IO[NotionError, NotionResponse] =
    ZIO.succeed(pagination.startCursor match {
      case Some("a") =>
        s"""{
           |    "object": "list",
           |    "results": [${blockPayload(fakeUUID)}],
           |    "next_cursor": null,
           |    "has_more": true
           |}""".stripMargin
      case _ =>
        s"""{
           |    "object": "list",
           |    "results": [${blockPayload(fakeUUID)}],
           |    "next_cursor": "a",
           |    "has_more": true
           |}""".stripMargin
    })

  override def queryDatabase(
      databaseId: String,
      query: Query,
      pagination: Pagination
  )(implicit trace: Trace): IO[NotionError, NotionResponse] =
    ZIO.succeed(
      pagination.startCursor match {
        case Some("a") =>
          s"""{
             |    "object": "list",
             |    "results": [${pagePayload(fakeUUID)}],
             |    "next_cursor": "b",
             |    "has_more": true
             |}""".stripMargin
        case Some("b") =>
          s"""{
             |    "object": "list",
             |    "results": [${pagePayload(fakeUUID)}],
             |    "next_cursor": null,
             |    "has_more": false
             |}""".stripMargin
        case _ =>
          s"""{
             |    "object": "list",
             |    "results": [${pagePayload(fakeUUID)}],
             |    "next_cursor": "a",
             |    "has_more": true
             |}""".stripMargin
      }
    )

  override def updatePage(
      pageId: String,
      operations: Page.Patch.StatelessOperations
  )(implicit trace: Trace): IO[NotionError, NotionResponse] = retrievePage(pageId)

  override def updatePage(page: Page, operations: Page.Patch.Operations)(implicit trace: Trace): IO[NotionError, NotionResponse] =
    retrievePage(page.id)

  override def updateDatabase(
      databaseId: String,
      operations: Database.Patch.StatelessOperations
  )(implicit trace: Trace): IO[NotionError, NotionResponse] = retrieveDatabase(databaseId)

  override def updateDatabase(
      database: Database,
      operations: Database.Patch.Operations
  )(implicit trace: Trace): IO[NotionError, NotionResponse] = retrieveDatabase(database.id)

  override def createDatabase(
      pageId: String,
      title: Seq[RichTextFragment],
      icon: Option[Icon],
      cover: Option[Cover],
      properties: Map[String, PatchedPropertyDefinition.PropertySchema]
  )(implicit trace: Trace): IO[NotionError, NotionResponse] = retrieveDatabase(fakeUUID)

  override def createPageInPage(
      parent: PageId,
      title: Option[PatchedProperty],
      icon: Option[Icon],
      cover: Option[Cover],
      children: Seq[BlockContent]
  )(implicit trace: Trace): IO[NotionError, NotionResponse] =
    ZIO.succeed(s"""
                   |{
                   |    "object": "page",
                   |    "id": "94e3ce7a-d7ad-444e-84be-2ead3c76eea7",
                   |    "created_time": "2022-06-10T08:23:00.000Z",
                   |    "last_edited_time": "2022-06-10T08:23:00.000Z",
                   |    "created_by": {
                   |        "object": "user",
                   |        "id": "755f50d3-d9ad-46b9-a69d-cd5048e8a436"
                   |    },
                   |    "last_edited_by": {
                   |        "object": "user",
                   |        "id": "755f50d3-d9ad-46b9-a69d-cd5048e8a436"
                   |    },
                   |    "cover": null,
                   |    "icon": null,
                   |    "parent": {
                   |        "type": "page_id",
                   |        "page_id": "${parent.pageId}"
                   |    },
                   |    "archived": false,
                   |    "properties": {
                   |        "title": {
                   |            "id": "title",
                   |            "type": "title",
                   |            "title": [
                   |                {
                   |                    "type": "text",
                   |                    "text": {
                   |                        "content": "hello",
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
                   |                    "plain_text": "hello",
                   |                    "href": null
                   |                }
                   |            ]
                   |        }
                   |    },
                   |    "url": "https://www.notion.so/hello-94e3ce7ad7ad444e84be2ead3c76eea7"
                   |}
                   |""".stripMargin)

  override def createPageInDatabase(
      parent: Parent.DatabaseId,
      properties: Map[String, PatchedProperty],
      icon: Option[Icon],
      cover: Option[Cover],
      children: Seq[BlockContent]
  )(implicit trace: Trace): IO[NotionError, NotionResponse] =
    ZIO.succeed(s"""
                   |{
                   |    "object": "page",
                   |    "id": "${parent.databaseId}",
                   |    "created_time": "2022-06-10T09:53:00.000Z",
                   |    "last_edited_time": "2022-06-10T09:53:00.000Z",
                   |    "created_by": {
                   |        "object": "user",
                   |        "id": "755f50d3-d9ad-46b9-a69d-cd5048e8a436"
                   |    },
                   |    "last_edited_by": {
                   |        "object": "user",
                   |        "id": "755f50d3-d9ad-46b9-a69d-cd5048e8a436"
                   |    },
                   |    "cover": null,
                   |    "icon": null,
                   |    "parent": {
                   |        "type": "database_id",
                   |        "database_id": "${parent.databaseId}"
                   |    },
                   |    "archived": false,
                   |    "properties": {
                   |        "Price": {
                   |            "id": "JLha",
                   |            "type": "number",
                   |            "number": 2.5
                   |        },
                   |        "Name": {
                   |            "id": "title",
                   |            "type": "title",
                   |            "title": [
                   |                {
                   |                    "type": "text",
                   |                    "text": {
                   |                        "content": "helloNotion",
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
                   |                    "plain_text": "helloNotion",
                   |                    "href": null
                   |                }
                   |            ]
                   |        }
                   |    },
                   |    "url": "https://www.notion.so/helloNotion-7dba62a154bb4481b3f0de767ed534d4"
                   |}
                   |""".stripMargin)
}

object TestNotionClient {
  val layer: ULayer[NotionClient] = ZLayer.succeed(TestNotionClient())
}
