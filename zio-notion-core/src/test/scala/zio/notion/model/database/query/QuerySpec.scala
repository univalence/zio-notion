package zio.notion.model.database.query

import io.circe.parser.decode
import io.circe.syntax._

import zio.Scope
import zio.notion.model.database.query.sort.{Sort, Sorts}
import zio.notion.model.database.query.sort.Sort._
import zio.notion.model.database.query.sort.TimestampType._
import zio.notion.model.user.User
import zio.test._
import zio.test.Assertion._

object QuerySpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("User serde suite")(
      test("We should be able to parse a user payload as json") {
        val exp = Query(Some(Sorts(List(Sort.Property("checkbox", ascending = true)))))

        println(exp.asJson)

        assert(decode[User](null))(isRight(equalTo(null)))
      },
      test("We should be able to parse a user payload as json") {
        val expSwag = "checkbox".ascending

        assert(decode[User](null))(isRight(equalTo(null)))
      },
      test("We should be able to parse a user payload as json") {
        val expSwag = createdTime.descending

        assert(decode[User](null))(isRight(equalTo(null)))
      },
      test("We should be able to parse a user payload as json") {
        val sorts   = "checkbox".ascending && createdTime.descending && "test".descending && "hello".descending
        val filters = ???

        assert(decode[User](null))(isRight(equalTo(null)))
      },
      test("We should be able to parse a user payload as json") {
        val expSwag = where checkbox "Done".isTrue
        and where multiselect("my tags").contains("todo")
        or multiselect "passive tags".contains("not done")

        assert(decode[User](null))(isRight(equalTo(null)))
      }
    )
}
