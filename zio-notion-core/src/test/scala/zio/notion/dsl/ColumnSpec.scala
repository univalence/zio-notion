package zio.notion.dsl

import zio.Scope
import zio.notion.Faker._
import zio.notion.model.database.query.{Filter, PropertyFilter}
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

        assertTrue(
          filter ==
            Number(fakeName, NumberPropertyFilter.Equals(2))
              .and(Number(fakeName, NumberPropertyFilter.LessThan(3)))
              .and(Number(fakeName, NumberPropertyFilter.DoesNotEqual(1)))
              .and(Number(fakeName, NumberPropertyFilter.GreaterThan(1)))
              .and(Number(fakeName, NumberPropertyFilter.LessThanOrEqualTo(2)))
              .and(Number(fakeName, NumberPropertyFilter.GreaterThanOrEqualTo(2)))
              .and(Number(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
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

        assertTrue(
          filter == Title(fakeName, TextPropertyFilter.StartsWith(fakeName))
            .and(Title(fakeName, TextPropertyFilter.EndsWith(fakeName)))
            .and(Title(fakeName, PropertyFilter.Equals(fakeName)))
            .and(Title(fakeName, PropertyFilter.DoesNotEqual("something else")))
            .and(Title(fakeName, PropertyFilter.Contains(fakeName)))
            .and(Title(fakeName, PropertyFilter.DoesNotContain("xyz")))
            .and(Title(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
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

        assertTrue(
          filter == RichText(fakeName, TextPropertyFilter.StartsWith(fakeName))
            .and(RichText(fakeName, TextPropertyFilter.EndsWith(fakeName)))
            .and(RichText(fakeName, PropertyFilter.Equals(fakeName)))
            .and(RichText(fakeName, PropertyFilter.DoesNotEqual("something else")))
            .and(RichText(fakeName, PropertyFilter.Contains(fakeName)))
            .and(RichText(fakeName, PropertyFilter.DoesNotContain("xyz")))
            .and(RichText(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
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

        assertTrue(
          filter == Date(fakeName, DatePropertyFilter.Before(LocalDate.MAX.toString))
            .and(Date(fakeName, DatePropertyFilter.After(LocalDate.MIN.toString)))
            .and(Date(fakeName, DatePropertyFilter.OnOrBefore(LocalDate.MAX.toString)))
            .and(Date(fakeName, DatePropertyFilter.OnOrAfter(LocalDate.MIN.toString)))
            .and(Date(fakeName, DatePropertyFilter.PastWeek))
            .and(Date(fakeName, DatePropertyFilter.PastMonth))
            .and(Date(fakeName, DatePropertyFilter.NextWeek))
            .and(Date(fakeName, DatePropertyFilter.NextMonth))
            .and(Date(fakeName, DatePropertyFilter.NextYear))
            .and(Date(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
      },
      test("creates a people filter") {
        val filter =
          $"$fakeName".asPeople contains fakeName and
            $"$fakeName".asPeople.doesNotContain("xyz") and
            $"$fakeName".asPeople.isNotEmpty
        assertTrue(
          filter == People(fakeName, Contains(fakeName))
            .and(People(fakeName, PropertyFilter.DoesNotContain("xyz")))
            .and(People(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
      },
      test("creates a select filter") {
        val filter =
          $"$fakeName".asSelect equals fakeName and
            $"$fakeName".asSelect.doesNotEqual("xyz") and
            $"$fakeName".asSelect.isNotEmpty
        assertTrue(
          filter == Select(fakeName, Equals(fakeName))
            .and(Select(fakeName, PropertyFilter.DoesNotEqual("xyz")))
            .and(Select(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
      },
      test("creates a multiselect filter") {
        val filter =
          $"$fakeName".asMultiSelect equals fakeName and
            $"$fakeName".asMultiSelect.doesNotEqual("xyz") and
            $"$fakeName".asMultiSelect.contains(fakeName) and
            $"$fakeName".asMultiSelect.doesNotContain("xyz") and
            $"$fakeName".asMultiSelect.isNotEmpty
        assertTrue(
          filter == MultiSelect(fakeName, Equals(fakeName))
            .and(MultiSelect(fakeName, PropertyFilter.DoesNotEqual("xyz")))
            .and(MultiSelect(fakeName, PropertyFilter.Contains(fakeName)))
            .and(MultiSelect(fakeName, PropertyFilter.DoesNotContain("xyz")))
            .and(MultiSelect(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
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
        assertTrue(
          filter == Url(fakeName, Equals(fakeName))
            .and(Url(fakeName, PropertyFilter.DoesNotEqual("xyz")))
            .and(Url(fakeName, PropertyFilter.Contains(fakeName)))
            .and(Url(fakeName, PropertyFilter.DoesNotContain("xyz")))
            .and(Url(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
      },
      test("creates an email filter") {
        val filter =
          $"$fakeName".asEmail equals fakeName and
            $"$fakeName".asEmail.doesNotEqual("xyz") and
            $"$fakeName".asEmail.contains(fakeName) and
            $"$fakeName".asEmail.doesNotContain("xyz") and
            $"$fakeName".asEmail.isNotEmpty
        assertTrue(
          filter == Email(fakeName, Equals(fakeName))
            .and(Email(fakeName, PropertyFilter.DoesNotEqual("xyz")))
            .and(Email(fakeName, PropertyFilter.Contains(fakeName)))
            .and(Email(fakeName, PropertyFilter.DoesNotContain("xyz")))
            .and(Email(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
      },
      test("creates a phn nbr filter") {
        val filter =
          $"$fakeName".asPhoneNumber equals fakeName and
            $"$fakeName".asPhoneNumber.doesNotEqual("xyz") and
            $"$fakeName".asPhoneNumber.contains(fakeName) and
            $"$fakeName".asPhoneNumber.doesNotContain("xyz") and
            $"$fakeName".asPhoneNumber.isNotEmpty
        assertTrue(
          filter == PhoneNumber(fakeName, Equals(fakeName))
            .and(PhoneNumber(fakeName, PropertyFilter.DoesNotEqual("xyz")))
            .and(PhoneNumber(fakeName, PropertyFilter.Contains(fakeName)))
            .and(PhoneNumber(fakeName, PropertyFilter.DoesNotContain("xyz")))
            .and(PhoneNumber(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
      },
      test("creates a relation filter") {
        val filter =
          $"$fakeName".asRelation.contains(fakeName) and
            $"$fakeName".asRelation.doesNotContain("xyz") and
            $"$fakeName".asRelation.isNotEmpty

        assertTrue(
          filter == Relation(fakeName, PropertyFilter.Contains(fakeName))
            .and(Relation(fakeName, PropertyFilter.DoesNotContain("xyz")))
            .and(Relation(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
      },
      test("creates a createdby filter") {
        val filter =
          $"$fakeName".asCreatedBy.contains(fakeName) and
            $"$fakeName".asCreatedBy.doesNotContain("xyz") and
            $"$fakeName".asCreatedBy.isNotEmpty
        assertTrue(
          filter == CreatedBy(fakeName, PropertyFilter.Contains(fakeName))
            .and(CreatedBy(fakeName, PropertyFilter.DoesNotContain("xyz")))
            .and(CreatedBy(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
      },
      test("creates a lastedited by filter") {
        val filter =
          $"$fakeName".asLastEditedBy.contains(fakeName) and
            $"$fakeName".asLastEditedBy.doesNotContain("xyz") and
            $"$fakeName".asLastEditedBy.isNotEmpty
        assertTrue(
          filter == LastEditedBy(fakeName, PropertyFilter.Contains(fakeName))
            .and(LastEditedBy(fakeName, PropertyFilter.DoesNotContain("xyz")))
            .and(LastEditedBy(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
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
        assertTrue(
          filter == CreatedTime(fakeName, DatePropertyFilter.Before(LocalDate.MAX.toString))
            .and(CreatedTime(fakeName, DatePropertyFilter.After(LocalDate.MIN.toString)))
            .and(CreatedTime(fakeName, DatePropertyFilter.OnOrBefore(LocalDate.MAX.toString)))
            .and(CreatedTime(fakeName, DatePropertyFilter.OnOrAfter(LocalDate.MIN.toString)))
            .and(CreatedTime(fakeName, DatePropertyFilter.PastWeek))
            .and(CreatedTime(fakeName, DatePropertyFilter.PastMonth))
            .and(CreatedTime(fakeName, DatePropertyFilter.NextWeek))
            .and(CreatedTime(fakeName, DatePropertyFilter.NextMonth))
            .and(CreatedTime(fakeName, DatePropertyFilter.NextYear))
            .and(CreatedTime(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
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
        assertTrue(
          filter == LastEditedTime(fakeName, DatePropertyFilter.Before(LocalDate.MAX.toString))
            .and(LastEditedTime(fakeName, DatePropertyFilter.After(LocalDate.MIN.toString)))
            .and(LastEditedTime(fakeName, DatePropertyFilter.OnOrBefore(LocalDate.MAX.toString)))
            .and(LastEditedTime(fakeName, DatePropertyFilter.OnOrAfter(LocalDate.MIN.toString)))
            .and(LastEditedTime(fakeName, DatePropertyFilter.PastWeek))
            .and(LastEditedTime(fakeName, DatePropertyFilter.PastMonth))
            .and(LastEditedTime(fakeName, DatePropertyFilter.NextWeek))
            .and(LastEditedTime(fakeName, DatePropertyFilter.NextMonth))
            .and(LastEditedTime(fakeName, DatePropertyFilter.NextYear))
            .and(LastEditedTime(fakeName, PropertyFilter.IsNotEmpty(true)))
        )
      },
      test("I can convert a column into a column definition") {
        val columnDefinition = col("col1").definition
        assertTrue(columnDefinition == ColumnDefinition("col1"))
      }
    )
}
