package zio.notion

import zio.{Scope, ZIO}
import zio.notion.Faker._
import zio.notion.model.database.Database
import zio.notion.model.page.Page
import zio.notion.model.user.{User, Users}
import zio.test._

object NotionSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Notion high level api suite")(
      test("User can retrieve a page from Notion") {
        val effect: ZIO[Notion, NotionError, Page] = Notion.retrievePage(fakeUUID)
        effect.provide(TestNotionClient.layer, Notion.live).map(page => assertTrue(page.id == fakeUUID))
      },
      test("User can retrieve a database from Notion") {
        val effect: ZIO[Notion, NotionError, Database] = Notion.retrieveDatabase(fakeUUID)
        effect.provide(TestNotionClient.layer, Notion.live).map(database => assertTrue(database.id == fakeUUID))
      },
      test("User can retrieve an user from Notion") {
        val effect: ZIO[Notion, NotionError, User] = Notion.retrieveUser(fakeUUID)
        effect
          .provide(TestNotionClient.layer, Notion.live)
          .map(user => assertTrue(user.isInstanceOf[User.Person]))
      },
      test("User can retrieve users from Notion") {
        val effect: ZIO[Notion, NotionError, Users] = Notion.retrieveUsers
        effect
          .provide(TestNotionClient.layer, Notion.live)
          .map(users => assertTrue(users.results.length == 2))
      }
    )
}
