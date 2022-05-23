package zio.notion.dsl

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.database.query.PropertyFilter._
import zio.notion.model.database.query.PropertyFilter.DatePropertyFilter.Before
import zio.test._

import java.time.LocalDate

object ColumnSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Colmun suite")(
      test("I can generate a column using string context") {
        val column = $"col1"
        assertTrue(column == Column("col1"))
      },
      test("I can generate a column using col function") {
        val column = col("col1")

        assertTrue(column == Column("col1"))
      },
      test("creates a number filter") {
        val filter = $"$fakeName".asNumber == 2
        assertTrue(filter == Number(fakeName, NumberPropertyFilter.Equals(2)))
      },
      test("creates a title filter") {
        val filter = $"$fakeName".asTitle startsWith fakeName
        assertTrue(filter == Title(fakeName, TextPropertyFilter.StartsWith(fakeName)))
      },
      test("creates a rich-text filter") {
        val filter = $"$fakeName".asRichText startsWith fakeName
        assertTrue(filter == RichText(fakeName, TextPropertyFilter.StartsWith(fakeName)))
      },
      test("creates a checkbox filter") {
        val filter: Checkbox = $"$fakeName".asCheckbox.isTrue
        assertTrue(filter == Checkbox(fakeName, CheckboxPropertyFilter.Equals(true)))
      },
      test("creates a date filter") {
        val filter = $"$fakeName".asDate before LocalDate.MAX
        assertTrue(filter == Date(fakeName, DatePropertyFilter.Before(LocalDate.MAX.toString)))
      },
      test("creates a people filter") {
        val filter = $"$fakeName".asPeople contains fakeName
        assertTrue(filter == People(fakeName, Contains(fakeName)))
      },
      test("creates a select filter") {
        val filter = $"$fakeName".asSelect equals fakeName
        assertTrue(filter == Select(fakeName, Equals(fakeName)))
      },
      test("creates a multiselect filter") {
        val filter = $"$fakeName".asMultiSelect equals fakeName
        assertTrue(filter == MultiSelect(fakeName, Equals(fakeName)))
      },
      test("creates a file filter") {
        val filter = $"$fakeName".asFiles.isEmpty
        assertTrue(filter == Files(fakeName, IsEmpty(true)))
      },
      test("creates a url filter") {
        val filter = $"$fakeName".asUrl equals fakeName
        assertTrue(filter == Url(fakeName, Equals(fakeName)))
      },
      test("creates an email filter") {
        val filter = $"$fakeName".asEmail equals fakeName
        assertTrue(filter == Email(fakeName, Equals(fakeName)))
      },
      test("creates a phn nbr filter") {
        val filter = $"$fakeName".asPhoneNumber equals fakePhoneNumber
        assertTrue(filter == PhoneNumber(fakeName, Equals(fakePhoneNumber)))
      },
      test("creates a relation filter") {
        val filter = $"$fakeName".asRelation contains fakeName
        assertTrue(filter == Relation(fakeName, Contains(fakeName)))
      },
      test("creates a createdby filter") {
        val filter = $"$fakeName".asCreatedBy contains fakeName
        assertTrue(filter == CreatedBy(fakeName, Contains(fakeName)))
      },
      test("creates a lastedited by filter") {
        val filter = $"$fakeName".asLastEditedBy contains fakeName
        assertTrue(filter == LastEditedBy(fakeName, Contains(fakeName)))
      },
      test("creates a createdtime filter") {
        val filter = $"$fakeName".asCreatedTime < LocalDate.MAX
        assertTrue(filter == CreatedTime(fakeName, Before(LocalDate.MAX.toString)))
      },
      test("creates a lasteditedtime filter") {
        val filter = $"$fakeName".asLastEditedTime < LocalDate.MAX
        assertTrue(filter == LastEditedTime(fakeName, Before(LocalDate.MAX.toString)))
      }
    )
}
