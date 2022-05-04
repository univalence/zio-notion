package zio.notion

import zio.{Scope, ZIO}
import zio.notion.Faker._
import zio.notion.model.database.Database
import zio.notion.model.page.Page
import zio.test._

object NotionSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("Notion high level api suite")(
      test("User can retrieve a page from Notion") {
        val effect: ZIO[Notion, NotionError, Page] = Notion(_.retrievePage(fakeUUID))
        effect.provide(TestNotionClient.layer, Notion.live).map(page => assertTrue(page.id == fakeUUID))
      },
      test("User can retrieve a database from Notion") {
        val effect: ZIO[Notion, NotionError, Database] = Notion(_.retrieveDatabase(fakeUUID))
        effect.provide(TestNotionClient.layer, Notion.live).map(database => assertTrue(database.id == fakeUUID))
      }
    )
}
