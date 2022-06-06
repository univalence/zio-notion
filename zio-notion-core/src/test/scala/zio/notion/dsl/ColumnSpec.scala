package zio.notion.dsl

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.database.query.{Filter, PropertyFilter}
import zio.notion.model.database.query.Filter.And
import zio.notion.model.database.query.Filter.One
import zio.notion.model.database.query.PropertyFilter._
import zio.test._

import java.time.LocalDate

object ColumnSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Column suite")(
      test("I can generate a column using string context") {
        val column = $"col1"
        assertTrue(column == Column("col1"))
      },
      test("I can generate a column using col function") {
        val column = col("col1")
        assertTrue(column == Column("col1"))
      },
      test("creates a number filter") {
        val filter: Filter =
          $"$fakeName".asNumber == 2 and
            $"$fakeName".asNumber < 3 and
            $"$fakeName".asNumber != 1 and
            $"$fakeName".asNumber > 1 and
            $"$fakeName".asNumber <= 2 and
            $"$fakeName".asNumber >= 2 and
            $"$fakeName".asNumber.isNotEmpty

        val expected =
          And(
            List(
              One(Number(fakeName, NumberPropertyFilter.Equals(2))),
              One(Number(fakeName, NumberPropertyFilter.LessThan(3))),
              One(Number(fakeName, NumberPropertyFilter.DoesNotEqual(1))),
              One(Number(fakeName, NumberPropertyFilter.GreaterThan(1))),
              One(Number(fakeName, NumberPropertyFilter.LessThanOrEqualTo(2))),
              One(Number(fakeName, NumberPropertyFilter.GreaterThanOrEqualTo(2))),
              One(Number(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("creates a title filter") {
        val filter =
          $"$fakeName".asTitle startsWith fakeName and
            ($"$fakeName".asTitle endsWith fakeName) and
            $"$fakeName".asTitle.equals(fakeName) and
            $"$fakeName".asTitle.doesNotEqual("something else") and
            $"$fakeName".asTitle.contains(fakeName) and
            $"$fakeName".asTitle.doesNotContain("xyz") and
            $"$fakeName".asTitle.isNotEmpty

        val expected =
          And(
            List(
              One(Title(fakeName, TextPropertyFilter.StartsWith(fakeName))),
              One(Title(fakeName, TextPropertyFilter.EndsWith(fakeName))),
              One(Title(fakeName, PropertyFilter.Equals(fakeName))),
              One(Title(fakeName, PropertyFilter.DoesNotEqual("something else"))),
              One(Title(fakeName, PropertyFilter.Contains(fakeName))),
              One(Title(fakeName, PropertyFilter.DoesNotContain("xyz"))),
              One(Title(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("creates a rich-text filter") {
        val filter =
          $"$fakeName".asRichText startsWith fakeName and
            $"$fakeName".asRichText.endsWith(fakeName) and
            $"$fakeName".asRichText.equals(fakeName) and
            $"$fakeName".asRichText.doesNotEqual("something else") and
            $"$fakeName".asRichText.contains(fakeName) and
            $"$fakeName".asRichText.doesNotContain("xyz") and
            $"$fakeName".asRichText.isNotEmpty

        val expected =
          And(
            List(
              One(RichText(fakeName, TextPropertyFilter.StartsWith(fakeName))),
              One(RichText(fakeName, TextPropertyFilter.EndsWith(fakeName))),
              One(RichText(fakeName, PropertyFilter.Equals(fakeName))),
              One(RichText(fakeName, PropertyFilter.DoesNotEqual("something else"))),
              One(RichText(fakeName, PropertyFilter.Contains(fakeName))),
              One(RichText(fakeName, PropertyFilter.DoesNotContain("xyz"))),
              One(RichText(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("creates a checkbox filter") {
        val filter: Checkbox  = $"$fakeName".asCheckbox.isTrue
        val filter2: Checkbox = $"$fakeName".asCheckbox.isFalse
        assertTrue(filter == Checkbox(fakeName, CheckboxPropertyFilter.Equals(true)))
        assertTrue(filter2 == Checkbox(fakeName, CheckboxPropertyFilter.Equals(false)))
      },
      test("creates a date filter") {

        val filter =
          $"$fakeName".asDate < LocalDate.MAX and
            $"$fakeName".asDate > LocalDate.MIN and
            $"$fakeName".asDate <= LocalDate.MAX and
            $"$fakeName".asDate >= LocalDate.MIN and
            $"$fakeName".asDate.pastWeek and
            $"$fakeName".asDate.pastMonth and
            $"$fakeName".asDate.nextWeek and
            $"$fakeName".asDate.nextMonth and
            $"$fakeName".asDate.nextYear and
            $"$fakeName".asDate.isNotEmpty

        val expected =
          And(
            List(
              One(Date(fakeName, DatePropertyFilter.Before(LocalDate.MAX.toString))),
              One(Date(fakeName, DatePropertyFilter.After(LocalDate.MIN.toString))),
              One(Date(fakeName, DatePropertyFilter.OnOrBefore(LocalDate.MAX.toString))),
              One(Date(fakeName, DatePropertyFilter.OnOrAfter(LocalDate.MIN.toString))),
              One(Date(fakeName, DatePropertyFilter.PastWeek)),
              One(Date(fakeName, DatePropertyFilter.PastMonth)),
              One(Date(fakeName, DatePropertyFilter.NextWeek)),
              One(Date(fakeName, DatePropertyFilter.NextMonth)),
              One(Date(fakeName, DatePropertyFilter.NextYear)),
              One(Date(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("creates a people filter") {
        val filter =
          $"$fakeName".asPeople contains fakeName and
            $"$fakeName".asPeople.doesNotContain("xyz") and
            $"$fakeName".asPeople.isNotEmpty

        val expected =
          And(
            List(
              One(People(fakeName, Contains(fakeName))),
              One(People(fakeName, PropertyFilter.DoesNotContain("xyz"))),
              One(People(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("creates a select filter") {
        val filter =
          $"$fakeName".asSelect equals fakeName and
            $"$fakeName".asSelect.doesNotEqual("xyz") and
            $"$fakeName".asSelect.isNotEmpty

        val expected =
          And(
            List(
              One(Select(fakeName, Equals(fakeName))),
              One(Select(fakeName, PropertyFilter.DoesNotEqual("xyz"))),
              One(Select(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("creates a multiselect filter") {
        val filter =
          $"$fakeName".asMultiSelect equals fakeName and
            $"$fakeName".asMultiSelect.doesNotEqual("xyz") and
            $"$fakeName".asMultiSelect.contains(fakeName) and
            $"$fakeName".asMultiSelect.doesNotContain("xyz") and
            $"$fakeName".asMultiSelect.isNotEmpty

        val expected =
          And(
            List(
              One(MultiSelect(fakeName, Equals(fakeName))),
              One(MultiSelect(fakeName, PropertyFilter.DoesNotEqual("xyz"))),
              One(MultiSelect(fakeName, PropertyFilter.Contains(fakeName))),
              One(MultiSelect(fakeName, PropertyFilter.DoesNotContain("xyz"))),
              One(MultiSelect(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("creates a file filter") {
        val filter  = $"$fakeName".asFiles.isEmpty
        val filter2 = $"$fakeName".asFiles.isNotEmpty
        assertTrue(filter == Files(fakeName, IsEmpty(true)))
        assertTrue(filter2 == Files(fakeName, IsNotEmpty(true)))
      },
      test("creates a url filter") {
        val filter =
          $"$fakeName".asUrl equals fakeName and
            $"$fakeName".asUrl.doesNotEqual("xyz") and
            $"$fakeName".asUrl.contains(fakeName) and
            $"$fakeName".asUrl.doesNotContain("xyz") and
            $"$fakeName".asUrl.isNotEmpty

        val expected =
          And(
            List(
              One(Url(fakeName, Equals(fakeName))),
              One(Url(fakeName, PropertyFilter.DoesNotEqual("xyz"))),
              One(Url(fakeName, PropertyFilter.Contains(fakeName))),
              One(Url(fakeName, PropertyFilter.DoesNotContain("xyz"))),
              One(Url(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("creates an email filter") {
        val filter =
          $"$fakeName".asEmail equals fakeName and
            $"$fakeName".asEmail.doesNotEqual("xyz") and
            $"$fakeName".asEmail.contains(fakeName) and
            $"$fakeName".asEmail.doesNotContain("xyz") and
            $"$fakeName".asEmail.isNotEmpty

        val expected =
          And(
            List(
              One(Email(fakeName, Equals(fakeName))),
              One(Email(fakeName, PropertyFilter.DoesNotEqual("xyz"))),
              One(Email(fakeName, PropertyFilter.Contains(fakeName))),
              One(Email(fakeName, PropertyFilter.DoesNotContain("xyz"))),
              One(Email(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("creates a phn nbr filter") {
        val filter =
          $"$fakeName".asPhoneNumber equals fakeName and
            $"$fakeName".asPhoneNumber.doesNotEqual("xyz") and
            $"$fakeName".asPhoneNumber.contains(fakeName) and
            $"$fakeName".asPhoneNumber.doesNotContain("xyz") and
            $"$fakeName".asPhoneNumber.isNotEmpty

        val expected =
          And(
            List(
              One(PhoneNumber(fakeName, Equals(fakeName))),
              One(PhoneNumber(fakeName, PropertyFilter.DoesNotEqual("xyz"))),
              One(PhoneNumber(fakeName, PropertyFilter.Contains(fakeName))),
              One(PhoneNumber(fakeName, PropertyFilter.DoesNotContain("xyz"))),
              One(PhoneNumber(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("creates a relation filter") {
        val filter =
          $"$fakeName".asRelation.contains(fakeName) and
            $"$fakeName".asRelation.doesNotContain("xyz") and
            $"$fakeName".asRelation.isNotEmpty

        val expected =
          And(
            List(
              One(Relation(fakeName, PropertyFilter.Contains(fakeName))),
              One(Relation(fakeName, PropertyFilter.DoesNotContain("xyz"))),
              One(Relation(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("creates a createdby filter") {
        val filter =
          $"$fakeName".asCreatedBy.contains(fakeName) and
            $"$fakeName".asCreatedBy.doesNotContain("xyz") and
            $"$fakeName".asCreatedBy.isNotEmpty

        val expected =
          And(
            List(
              One(CreatedBy(fakeName, PropertyFilter.Contains(fakeName))),
              One(CreatedBy(fakeName, PropertyFilter.DoesNotContain("xyz"))),
              One(CreatedBy(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("creates a lastedited by filter") {
        val filter =
          $"$fakeName".asLastEditedBy.contains(fakeName) and
            $"$fakeName".asLastEditedBy.doesNotContain("xyz") and
            $"$fakeName".asLastEditedBy.isNotEmpty

        val expected =
          And(
            List(
              One(LastEditedBy(fakeName, PropertyFilter.Contains(fakeName))),
              One(LastEditedBy(fakeName, PropertyFilter.DoesNotContain("xyz"))),
              One(LastEditedBy(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )
        assertTrue(filter == expected)
      },
      test("creates a createdtime filter") {
        val filter =
          $"$fakeName".asCreatedTime < LocalDate.MAX and
            $"$fakeName".asCreatedTime > LocalDate.MIN and
            $"$fakeName".asCreatedTime <= LocalDate.MAX and
            $"$fakeName".asCreatedTime >= LocalDate.MIN and
            $"$fakeName".asCreatedTime.pastWeek and
            $"$fakeName".asCreatedTime.pastMonth and
            $"$fakeName".asCreatedTime.nextWeek and
            $"$fakeName".asCreatedTime.nextMonth and
            $"$fakeName".asCreatedTime.nextYear and
            $"$fakeName".asCreatedTime.isNotEmpty

        val expected =
          And(
            List(
              One(CreatedTime(fakeName, DatePropertyFilter.Before(LocalDate.MAX.toString))),
              One(CreatedTime(fakeName, DatePropertyFilter.After(LocalDate.MIN.toString))),
              One(CreatedTime(fakeName, DatePropertyFilter.OnOrBefore(LocalDate.MAX.toString))),
              One(CreatedTime(fakeName, DatePropertyFilter.OnOrAfter(LocalDate.MIN.toString))),
              One(CreatedTime(fakeName, DatePropertyFilter.PastWeek)),
              One(CreatedTime(fakeName, DatePropertyFilter.PastMonth)),
              One(CreatedTime(fakeName, DatePropertyFilter.NextWeek)),
              One(CreatedTime(fakeName, DatePropertyFilter.NextMonth)),
              One(CreatedTime(fakeName, DatePropertyFilter.NextYear)),
              One(CreatedTime(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("creates a lasteditedtime filter") {
        val filter =
          $"$fakeName".asLastEditedTime < LocalDate.MAX and
            $"$fakeName".asLastEditedTime > LocalDate.MIN and
            $"$fakeName".asLastEditedTime <= LocalDate.MAX and
            $"$fakeName".asLastEditedTime >= LocalDate.MIN and
            $"$fakeName".asLastEditedTime.pastWeek and
            $"$fakeName".asLastEditedTime.pastMonth and
            $"$fakeName".asLastEditedTime.nextWeek and
            $"$fakeName".asLastEditedTime.nextMonth and
            $"$fakeName".asLastEditedTime.nextYear and
            $"$fakeName".asLastEditedTime.isNotEmpty

        val expected =
          And(
            List(
              One(LastEditedTime(fakeName, DatePropertyFilter.Before(LocalDate.MAX.toString))),
              One(LastEditedTime(fakeName, DatePropertyFilter.After(LocalDate.MIN.toString))),
              One(LastEditedTime(fakeName, DatePropertyFilter.OnOrBefore(LocalDate.MAX.toString))),
              One(LastEditedTime(fakeName, DatePropertyFilter.OnOrAfter(LocalDate.MIN.toString))),
              One(LastEditedTime(fakeName, DatePropertyFilter.PastWeek)),
              One(LastEditedTime(fakeName, DatePropertyFilter.PastMonth)),
              One(LastEditedTime(fakeName, DatePropertyFilter.NextWeek)),
              One(LastEditedTime(fakeName, DatePropertyFilter.NextMonth)),
              One(LastEditedTime(fakeName, DatePropertyFilter.NextYear)),
              One(LastEditedTime(fakeName, PropertyFilter.IsNotEmpty(true)))
            )
          )

        assertTrue(filter == expected)
      },
      test("I can convert a column into a column definition") {
        val columnDefinition = col("col1").definition
        assertTrue(columnDefinition == ColumnDefinition("col1"))
      }
    )
}
