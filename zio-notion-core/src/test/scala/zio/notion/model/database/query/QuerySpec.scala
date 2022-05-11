package zio.notion.model.database.query

import io.circe.parser.decode
import io.circe.syntax._

import zio.Scope
import zio.notion.model.database.query.filter.{Filter, PropertyFilter}
import zio.notion.model.database.query.filter.Filter._
import zio.notion.model.database.query.filter.PropertyFilter.{
  Checkbox,
  CheckboxPropertyFilter,
  Date,
  DatePropertyFilter,
  MultiSelect,
  TextPropertyFilter,
  Title
}
import zio.notion.model.database.query.filter.PropertyFilter.Checkbox.checkbox
import zio.notion.model.database.query.filter.PropertyFilter.MultiSelect.multiSelect
import zio.notion.model.database.query.filter.PropertyFilter.Title.title
import zio.notion.model.database.query.sort.{Sort, Sorts}
import zio.notion.model.database.query.sort.Sort._
import zio.notion.model.database.query.sort.TimestampType._
import zio.notion.model.printer
import zio.notion.model.user.User
import zio.test._
import zio.test.Assertion._

object QuerySpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("User serde suite")(
      test("We should be able to parse a user payload as json") {
        val exp = Query(None, Some(Sorts(List(Sort.Property("checkbox", ascending = true)))))

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
//        val expSwag = where checkbox "Done".isTrue
//        and where multiselect("my tags").contains("todo")
//        or multiselect "passive tags".contains("not done")

        val exp: Filter =
          And(
            List(
              SubFilter.PropFilter(Title(TextPropertyFilter.StartsWith("truc"), "chose")),
              SubFilter.PropFilter(Date(DatePropertyFilter.PastWeek, "machin"))
            )
          )

        val sorts          = "checkbox".ascending && createdTime.descending && "test".descending && "hello".descending
        val filter: String = """ title("chose").startsWith("truc") && pastWeek""".stripMargin

        assertTrue(printer.print(exp.asJson) == "")
      },
      test("We should be able to parse a user payload as json") {
        //        val expSwag = where checkbox "Done".isTrue
        //        and where multiselect("my tags").contains("todo")
        //        or multiselect "passive tags".contains("not done")

        val exp: Filter =
          And(
            List(
              SubFilter.PropFilter(Title(TextPropertyFilter.StartsWith("truc"), "chose")),
              SubFilter.PropFilter(Date(DatePropertyFilter.PastWeek, "machin"))
            )
          )

        val exp2: Filter =
          Or(
            List(
              SubFilter.Or(
                List(
                  Checkbox(CheckboxPropertyFilter.DoesNotEqual(true), "nope"),
                  MultiSelect(PropertyFilter.DoesNotContain("yes"), "yep")
                )
              ),
              SubFilter.PropFilter(Title(TextPropertyFilter.StartsWith("truc"), "chose")),
              SubFilter.And(
                List(
                  Title(TextPropertyFilter.StartsWith("hell"), "chose2"),
                  Title(TextPropertyFilter.EndsWith("o"), "chose3"),
                  Date(DatePropertyFilter.PastWeek, "machin")
                )
              )
            )
          )

        val sorts = "checkbox".ascending && createdTime.descending && "test".descending && "hello".descending

        val filter2: Filter =
          where(title("chose").startsWith("truc")) or
            where(title("chose2").startsWith("truc") and title("chose2").startsWith("truc")) or
            where(
              checkbox("nope").isNotTrue
                or multiSelect("yep").doesNotContain("yes")
                or multiSelect("yep").doesNotContain("yes")
            )

        assertTrue(printer.print(exp.asJson) == "")
      }
    )
}
