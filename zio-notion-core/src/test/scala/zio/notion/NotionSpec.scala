package zio.notion

import zio.{Scope, ZIO}
import zio.notion.Faker._
import zio.notion.Faker.FakePatchedProperty.{fakePatchedNumber, fakePatchedTitle}
import zio.notion.dsl._
import zio.notion.model.common.Parent.Workspace.StringOps
import zio.notion.model.database.{Database, DatabaseQuery}
import zio.notion.model.database.query.Query
import zio.notion.model.page.Page
import zio.notion.model.user.{User, Users}
import zio.test._

object NotionSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Notion high level api suite")(
      test("User can retrieve a page from Notion") {
        val effect: ZIO[Notion, NotionError, Page] = Notion.retrievePage(fakeUUID)

        effect.map(page => assertTrue(page.id == fakeUUID))
      },
      test("User can retrieve a database from Notion") {
        val effect: ZIO[Notion, NotionError, Database] = Notion.retrieveDatabase(fakeUUID)

        effect.map(database => assertTrue(database.id == fakeUUID))
      },
      test("User can query a database from Notion") {
        val effect: ZIO[Notion, NotionError, DatabaseQuery] = Notion.queryAllDatabase(fakeUUID)

        effect.map(database => assertTrue(database.results.length == 3))
      },
      test("User can retrieve an user from Notion") {
        val effect: ZIO[Notion, NotionError, User] = Notion.retrieveUser(fakeUUID)

        effect.map(user => assertTrue(user.isInstanceOf[User.Person]))
      },
      test("User can retrieve users from Notion") {
        val effect: ZIO[Notion, NotionError, Users] = Notion.retrieveUsers

        effect.map(users => assertTrue(users.results.length == 2))
      },
      test("User can query a database") {
        val effect: ZIO[Notion, NotionError, DatabaseQuery] = Notion.queryDatabase(fakeUUID, Query.empty, Pagination.default)

        effect.map(res => assertTrue(res.results.length == 1))
      },
      test("User can update a page") {
        val effect: ZIO[Notion, NotionError, Page] = Notion.updatePage(fakePage.id, removeIcon)

        effect.map(res => assertTrue(res.id == fakeUUID))
      },
      test("User can update a database") {
        val effect: ZIO[Notion, NotionError, Database] = Notion.updateDatabase(fakeDatabase.id, setDatabaseTitle("Test"))

        effect.map(res => assertTrue(res.id == fakeUUID))
      },
      test("User can create a database") {
        val effect: ZIO[Notion, NotionError, Database] =
          Notion.createDatabase(fakeUUID, fakeDatabase.title, None, None, fakePropertyDefinitions)

        effect.map(res => assertTrue(res.id == fakeUUID))
      },
      test("User can create an empty page") {
        val effect: ZIO[Notion, NotionError, Page] = Notion.createPage(fakeUUID.asParentPage, Some(fakePatchedTitle), None, None)

        effect.map(res => assertTrue(res.parent == fakeUUID.asParentPage))
      },
      test("User can create an empty page in database") {
        val effect: ZIO[Notion, NotionError, Page] =
          Notion.createPageInDatabase(fakeUUID.asParentDatabase, Map("Name" -> fakePatchedTitle, "Price" -> fakePatchedNumber), None, None)

        effect.map(res => assertTrue(res.parent == fakeUUID.asParentDatabase))
      }
    ).provide(TestNotionClient.layer, Notion.live)
}
