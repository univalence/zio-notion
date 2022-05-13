package zio.notion.model.page.patch

import io.circe.syntax.EncoderOps

import zio.{Scope, UIO}
import zio.notion.Faker._
import zio.notion.PropertyUpdater._
import zio.notion.model.common.{richtext, Url, UserId}
import zio.notion.model.common.enumeration.Color
import zio.notion.model.common.richtext.RichTextData
import zio.notion.model.page.patch.PatchedProperty._
import zio.notion.model.page.property.Link
import zio.notion.model.page.property.Link.External
import zio.notion.model.printer
import zio.test._

import java.time.LocalDate

object PatchedPropertySpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Test property patching action")(
      specPatchedNumber,
      specPatchedUrl,
      specPatchedSelect,
      specPatchedMultiSelect,
      specPatchedDate,
      specPatchedEmail,
      specPatchedCheckbox,
      specPatchedFiles,
      specPatchedTitle,
      specPatchedRichText,
      specPatchedPeople
    ) + specEncoding

  def specPatchedNumber: Spec[TestEnvironment with Scope, Any] = {
    def assertUpdate[E](updater: FieldUpdater[Nothing, PatchedNumber], initial: Double, excepted: Double): TestResult =
      assertTrue(updater.transform(PatchedNumber(initial)).map(_.number) == Right(excepted))

    suite("Test patching numbers")(
      test("We can set a new number") {
        val patch: FieldSetter[PatchedNumber] = PatchedNumber.set(10).onAll

        assertTrue(patch.value.number == 10)
      },
      test("We can add by a number") {
        val patch: FieldUpdater[Nothing, PatchedNumber] = PatchedNumber.add(10).onAll

        assertUpdate(patch, 20, 30)
      },
      test("We can subtract by a number") {
        val patch: FieldUpdater[Nothing, PatchedNumber] = PatchedNumber.minus(10).onAll

        assertUpdate(patch, 20, 10)
      },
      test("We can multiply by a number") {
        val patch: FieldUpdater[Nothing, PatchedNumber] = PatchedNumber.times(2).onAll

        assertUpdate(patch, 5, 10)
      },
      test("We can divide by a number") {
        val patch: FieldUpdater[Nothing, PatchedNumber] = PatchedNumber.divide(2).onAll

        assertUpdate(patch, 5, 2.5)
      },
      test("We can pow by a number") {
        val patch: FieldUpdater[Nothing, PatchedNumber] = PatchedNumber.pow(2).onAll

        assertUpdate(patch, 5, 25)
      },
      test("We can ceil a number") {
        val patch: FieldUpdater[Nothing, PatchedNumber] = PatchedNumber.ceil.onAll

        assertUpdate(patch, 5.4, 6)
      },
      test("We can floor a number") {
        val patch: FieldUpdater[Nothing, PatchedNumber] = PatchedNumber.floor.onAll

        assertUpdate(patch, 5.4, 5)
      }
    )
  }

  def specPatchedUrl: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching urls")(
      test("We can set a new url") {
        val patch: FieldSetter[PatchedUrl] = PatchedUrl.set(fakeUrl).onAll

        assertTrue(patch.value.url == fakeUrl)
      }
    )

  def specPatchedSelect: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching selects")(
      test("We can set a new select using its name") {
        val patch: FieldSetter[PatchedSelect] = PatchedSelect.setUsingName("name").onAll

        assertTrue(patch.value.name.contains("name") && patch.value.id.isEmpty)
      },
      test("We can set a new select using its id") {
        val patch: FieldSetter[PatchedSelect] = PatchedSelect.setUsingId("id").onAll

        assertTrue(patch.value.id.contains("id") && patch.value.name.isEmpty)
      }
    )

  def specPatchedMultiSelect: Spec[TestEnvironment with Scope, Any] = {
    def assertUpdate[E](
        updater: FieldUpdater[Nothing, PatchedMultiSelect],
        initial: List[PatchedSelect],
        expected: List[PatchedSelect]
    ): TestResult = assertTrue(updater.transform(PatchedMultiSelect(initial)).map(_.multiSelect) == Right(expected))

    suite("Test patching multi selects")(
      test("We can set a new multi select") {
        val multiSelect: List[PatchedSelect] = List(PatchedSelect(None, Some("name")))

        val patch: FieldSetter[PatchedMultiSelect] = PatchedMultiSelect.set(multiSelect).onAll

        assertTrue(patch.value.multiSelect == multiSelect)
      },
      test("We can remove a select using the name") {
        val multiSelect: List[PatchedSelect] = List(PatchedSelect(None, Some("name")), PatchedSelect(None, Some("other")))

        val patch: FieldUpdater[Nothing, PatchedMultiSelect] = PatchedMultiSelect.removeUsingNameIfExists("name").onAll

        assertUpdate(patch, multiSelect, List(PatchedSelect(None, Some("other"))))
      },
      test("We can remove a select using the id") {
        val multiSelect: List[PatchedSelect] = List(PatchedSelect(Some("id"), None), PatchedSelect(Some("other"), None))

        val patch: FieldUpdater[Nothing, PatchedMultiSelect] = PatchedMultiSelect.removeUsingIdIfExists("id").onAll

        assertUpdate(patch, multiSelect, List(PatchedSelect(Some("other"), None)))
      },
      test("We can add a select using the name") {
        val patch: FieldUpdater[Nothing, PatchedMultiSelect] = PatchedMultiSelect.addUsingName("name").onAll

        assertUpdate(patch, List.empty, List(PatchedSelect(None, Some("name"))))
      },
      test("We can add a select using the id") {
        val patch: FieldUpdater[Nothing, PatchedMultiSelect] = PatchedMultiSelect.addUsingId("id").onAll

        assertUpdate(patch, List.empty, List(PatchedSelect(Some("id"), None)))
      }
    )
  }

  def specPatchedDate: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching dates")(
      test("We can set a start date") {
        val patch: FieldSetter[PatchedDate] = PatchedDate.startAt(fakeLocalDate).onAll

        assertTrue(patch.value.start == fakeLocalDate)
      },
      test("We can add an end date") {
        val patch: FieldUpdater[Nothing, PatchedDate] = PatchedDate.endAt(fakeLocalDate.plusDays(2)).onAll

        assertTrue(patch.transform(PatchedDate(fakeLocalDate, None, None)).map(_.end) == Right(Some(fakeLocalDate.plusDays(2))))
      },
      test("We can set a date between two dates") {
        val patch: FieldSetter[PatchedDate] = PatchedDate.between(fakeLocalDate, fakeLocalDate.plusDays(2)).onAll

        assertTrue(patch.value.start == fakeLocalDate && patch.value.end.contains(fakeLocalDate.plusDays(2)))
      },
      test("We can set a start date to today") {
        val patch: UIO[FieldSetter[PatchedDate]] = PatchedDate.today.map(_.onAll)

        patch.map(p => assertTrue(p.value.start == LocalDate.of(1970, 1, 1)))
      }
    )

  def specPatchedEmail: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching emails")(
      test("We can set a new email") {
        val patch: FieldSetter[PatchedEmail] = PatchedEmail.set(fakeEmail).onAll

        assertTrue(patch.value.email == fakeEmail)
      }
    )

  def specPatchedPhoneNumber: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching phone numbers")(
      test("We can set a new phone number") {
        val patch: FieldSetter[PatchedPhoneNumber] = PatchedPhoneNumber.set(fakePhoneNumber).onAll

        assertTrue(patch.value.phoneNumber == fakePhoneNumber)
      }
    )

  def specPatchedCheckbox: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching checkbox")(
      test("We can check a checkbox") {
        val patch: FieldSetter[PatchedCheckbox] = PatchedCheckbox.check.onAll

        assertTrue(patch.value.checkbox)
      },
      test("We can uncheck a checkbox") {
        val patch: FieldSetter[PatchedCheckbox] = PatchedCheckbox.uncheck.onAll

        assertTrue(!patch.value.checkbox)
      },
      test("We can reverse a checkbox") {
        val patch: FieldUpdater[Nothing, PatchedCheckbox] = PatchedCheckbox.reverse.onAll

        assertTrue(patch.transform(PatchedCheckbox(false)).map(_.checkbox) == Right(true))
      }
    )

  def specPatchedFiles: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching files")(
      test("We can set a list of files") {
        val files: List[Link] = List(External("name", Url(fakeUrl)))

        val patch: FieldSetter[PatchedFiles] = PatchedFiles.set(files).onAll

        assertTrue(patch.value.files == files)
      },
      test("We can set a new file") {
        val patch: FieldUpdater[Nothing, PatchedFiles] = PatchedFiles.add(External("name", Url(fakeUrl))).onAll

        assertTrue(patch.transform(PatchedFiles(Seq.empty)).map(_.files) == Right(Seq(External("name", Url(fakeUrl)))))
      },
      test("We can filter files") {
        val files: List[Link] = List(External("name", Url(fakeUrl)))

        val patch: FieldUpdater[Nothing, PatchedFiles] =
          PatchedFiles.filter {
            case Link.File(name, _) => name != "name"
            case External(name, _)  => name != "name"
          }.onAll

        assertTrue(patch.transform(PatchedFiles(files)).map(_.files) == Right(Seq.empty))
      }
    )

  def specPatchedTitle: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching title")(
      test("We can set a new title") {
        val patch: FieldSetter[PatchedTitle] = PatchedTitle.set("Title").onAll

        assertTrue(patch.value.title.head.asInstanceOf[RichTextData.Text].plainText == "Title")
      },
      test("We can capitalize a title") {
        val patch: FieldUpdater[Nothing, PatchedTitle] = PatchedTitle.capitalize.onAll

        val source = PatchedTitle(List(RichTextData.default("title", richtext.Annotations.default)))

        assertTrue(patch.transform(source).map(_.title.head.asInstanceOf[RichTextData.Text].plainText) == Right("Title"))
      }
    )

  def specPatchedRichText: Spec[TestEnvironment with Scope, Any] = {
    def testAnnotation(name: String, transformation: UTransformation[PatchedRichText], expected: richtext.Annotations => Boolean) =
      test(s"We can use $name on a every rich text") {
        val default: PatchedRichText =
          PatchedRichText(
            List(
              RichTextData.Text(
                RichTextData.Text.TextData("This is a content", None),
                richtext.Annotations.default,
                "This is a content",
                None
              )
            )
          )

        val patch: FieldUpdater[Nothing, PatchedRichText] = transformation.onAll

        assertTrue(
          patch.transform(default).map(_.richText.map(_.asInstanceOf[RichTextData.Text].annotations).forall(expected)) == Right(true)
        )
      }

    suite("Test patching rich text")(
      test("We can write a new rich text") {
        val patch: FieldSetter[PatchedRichText] = PatchedRichText.write("A new content").onAll

        assertTrue(patch.value.richText.headOption.map(_.asInstanceOf[RichTextData.Text].plainText).contains("A new content"))
      },
      testAnnotation("reset", PatchedRichText.reset, _ == richtext.Annotations.default),
      testAnnotation("bold", PatchedRichText.bold, _.bold),
      testAnnotation("italic", PatchedRichText.italic, _.italic),
      testAnnotation("strikethrough", PatchedRichText.strikethrough, _.strikethrough),
      testAnnotation("underline", PatchedRichText.underline, _.underline),
      testAnnotation("code", PatchedRichText.code, _.code),
      testAnnotation("color", PatchedRichText.color(Color.BlueBackground), _.color == Color.BlueBackground)
    )
  }

  def specPatchedPeople: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching people")(
      test("We can set a list of people") {
        val people: List[UserId] = List(UserId(fakeUUID))

        val patch: FieldSetter[PatchedPeople] = PatchedPeople.set(people).onAll

        assertTrue(patch.value.people == people)
      },
      test("We can set a new person") {
        val patch: FieldUpdater[Nothing, PatchedPeople] = PatchedPeople.add(UserId(fakeUUID)).onAll

        assertTrue(patch.transform(PatchedPeople(Seq.empty)).map(_.people) == Right(Seq(UserId(fakeUUID))))
      }
    )

  def specEncoding: Spec[TestEnvironment with Scope, Any] =
    suite("Test property encoding")(
      test("PatchedNumber encoding") {
        val property: PatchedNumber = PatchedNumber(10)

        val expected: String =
          """{
            |  "number" : 10.0
            |}""".stripMargin

        assertTrue(printer.print(property.asJson) == expected)
      },
      test("PatchedUrl encoding") {
        val property: PatchedUrl = PatchedUrl(fakeUrl)

        val expected: String =
          s"""{
             |  "url" : "$fakeUrl"
             |}""".stripMargin

        assertTrue(printer.print(property.asJson) == expected)
      },
      test("PatchedSelect encoding") {
        val property: PatchedSelect = PatchedSelect(None, name = Some("My Select"))

        val expected: String =
          s"""{
             |  "select" : {
             |    "name" : "My Select"
             |  }
             |}""".stripMargin

        assertTrue(printer.print(property.asJson) == expected)
      },
      test("PatchedMultiSelect encoding") {
        val property: PatchedMultiSelect = PatchedMultiSelect(List(PatchedSelect(None, Some("name")), PatchedSelect(None, Some("other"))))

        val expected: String =
          s"""{
             |  "multi_select" : [
             |    {
             |      "name" : "name"
             |    },
             |    {
             |      "name" : "other"
             |    }
             |  ]
             |}""".stripMargin

        assertTrue(printer.print(property.asJson) == expected)
      },
      test("PatchedDate encoding") {
        val property: PatchedDate = PatchedDate(fakeLocalDate, Some(fakeLocalDate.plusDays(2)), Some("America/New_York"))

        val expected: String =
          s"""{
             |  "date" : {
             |    "start" : "2022-02-22",
             |    "end" : "2022-02-24",
             |    "time_zone" : "America/New_York"
             |  }
             |}""".stripMargin

        assertTrue(printer.print(property.asJson) == expected)
      },
      test("PatchedEmail encoding") {
        val property: PatchedEmail = PatchedEmail(fakeEmail)

        val expected: String =
          s"""{
             |  "email" : "$fakeEmail"
             |}""".stripMargin

        assertTrue(printer.print(property.asJson) == expected)
      },
      test("PatchedPhoneNumber encoding") {
        val property: PatchedPhoneNumber = PatchedPhoneNumber(fakePhoneNumber)

        val expected: String =
          s"""{
             |  "phone_number" : "$fakePhoneNumber"
             |}""".stripMargin

        assertTrue(printer.print(property.asJson) == expected)
      },
      test("PatchedCheckbox encoding") {
        val property: PatchedCheckbox = PatchedCheckbox(true)

        val expected: String =
          s"""{
             |  "checkbox" : true
             |}""".stripMargin

        assertTrue(printer.print(property.asJson) == expected)
      },
      test("PatchedFiles encoding") {
        val property: PatchedFiles = PatchedFiles(List(External("name", Url(fakeUrl))))

        val expected: String =
          s"""{
             |  "files" : [
             |    {
             |      "name" : "name",
             |      "external" : {
             |        "url" : "https://notion.zio"
             |      },
             |      "type" : "external"
             |    }
             |  ]
             |}""".stripMargin

        assertTrue(printer.print(property.asJson) == expected)
      },
      test("PatchedTitle encoding") {
        val property: PatchedTitle = PatchedTitle(List(RichTextData.default("Title", richtext.Annotations.default)))

        val expected: String =
          s"""{
             |  "title" : [
             |    {
             |      "text" : {
             |        "content" : "Title",
             |        "link" : null
             |      },
             |      "annotations" : {
             |        "bold" : false,
             |        "italic" : false,
             |        "strikethrough" : false,
             |        "underline" : false,
             |        "code" : false,
             |        "color" : "default"
             |      },
             |      "plain_text" : "Title",
             |      "href" : null,
             |      "type" : "text"
             |    }
             |  ]
             |}""".stripMargin

        assertTrue(printer.print(property.asJson) == expected)
      },
      test("PatchedRichText encoding") {
        val property: PatchedRichText =
          PatchedRichText(
            List(
              RichTextData.Text(
                RichTextData.Text.TextData("This is a content", None),
                richtext.Annotations.default,
                "This is a content",
                None
              )
            )
          )

        val expected: String =
          s"""{
             |  "rich_text" : [
             |    {
             |      "text" : {
             |        "content" : "This is a content",
             |        "link" : null
             |      },
             |      "annotations" : {
             |        "bold" : false,
             |        "italic" : false,
             |        "strikethrough" : false,
             |        "underline" : false,
             |        "code" : false,
             |        "color" : "default"
             |      },
             |      "plain_text" : "This is a content",
             |      "href" : null,
             |      "type" : "text"
             |    }
             |  ]
             |}""".stripMargin

        assertTrue(printer.print(property.asJson) == expected)
      },
      test("PatchedPeople encoding") {
        val property: PatchedPeople = PatchedPeople(List(UserId(fakeUUID)))

        val expected: String =
          s"""{
             |  "people" : [
             |    {
             |      "id" : "$fakeUUID"
             |    }
             |  ]
             |}""".stripMargin

        assertTrue(printer.print(property.asJson) == expected)
      }
    )
}
