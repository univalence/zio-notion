package zio.notion

import zio.Scope
import zio.test._

object NotionSpec extends ZIOSpecDefault {
  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("Notion high level api suite")(
      test("User can retrieve a page from Notion") {
        val effect = Notion(_.retrievePage("xxxx"))
        effect.provide(TestNotionClient.layer, Notion.live).map(page => assertTrue(page.id == "xxxx"))
      }
    )
}
