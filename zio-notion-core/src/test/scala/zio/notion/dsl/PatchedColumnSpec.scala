package zio.notion.dsl

import io.circe.syntax.EncoderOps

import zio.{Scope, UIO}
import zio.notion.Faker._
import zio.notion.model.common.{richtext, Id, Url}
import zio.notion.model.common.enumeration.Color
import zio.notion.model.common.richtext.RichTextData
import zio.notion.model.page.Page.Patch.Operations.Operation._
import zio.notion.model.page.PatchedProperty.{
  PatchedCheckbox,
  PatchedDate,
  PatchedEmail,
  PatchedFiles,
  PatchedMultiSelect,
  PatchedNumber,
  PatchedPeople,
  PatchedPhoneNumber,
  PatchedRichText,
  PatchedSelect,
  PatchedTitle,
  PatchedUrl
}
import zio.notion.model.page.property.Link
import zio.notion.model.page.property.Link.External
import zio.notion.model.printer
import zio.test.{assertTrue, Spec, TestEnvironment, TestResult, ZIOSpecDefault}

import java.time.{OffsetDateTime, ZoneOffset}

object PatchedColumnSpec extends ZIOSpecDefault {

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
    def assertUpdate[E](patch: UpdateProperty[PatchedNumber], initial: Double, excepted: Double): TestResult =
      assertTrue(patch.transform(PatchedNumber(initial)).map(_.number) == Right(excepted))

    suite("Test patching numbers")(
      test("We can set a new number") {
        val patch: SetProperty[PatchedNumber] = $"col".asNumber.patch.set(10)

        assertTrue(patch.value.number == 10)
      },
      test("We can add by a number") {
        val patch: UpdateProperty[PatchedNumber] = $"col".asNumber.patch.add(10)

        assertUpdate(patch, 20, 30)
      },
      test("We can subtract by a number") {
        val patch: UpdateProperty[PatchedNumber] = $"col".asNumber.patch.minus(10)

        assertUpdate(patch, 20, 10)
      },
      test("We can multiply by a number") {
        val patch: UpdateProperty[PatchedNumber] = $"col".asNumber.patch.times(2)

        assertUpdate(patch, 5, 10)
      },
      test("We can divide by a number") {
        val patch: UpdateProperty[PatchedNumber] = $"col".asNumber.patch.divide(2)

        assertUpdate(patch, 5, 2.5)
      },
      test("We can pow by a number") {
        val patch: UpdateProperty[PatchedNumber] = $"col".asNumber.patch.pow(2)

        assertUpdate(patch, 5, 25)
      },
      test("We can ceil a number") {
        val patch: UpdateProperty[PatchedNumber] = $"col".asNumber.patch.ceil

        assertUpdate(patch, 5.4, 6)
      },
      test("We can floor a number") {
        val patch: UpdateProperty[PatchedNumber] = $"col".asNumber.patch.floor

        assertUpdate(patch, 5.4, 5)
      }
    )
  }

  def specPatchedUrl: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching urls")(
      test("We can set a new url") {
        val patch: SetProperty[PatchedUrl] = $"col".asUrl.patch.set(fakeUrl)

        assertTrue(patch.value.url == fakeUrl)
      }
    )

  def specPatchedSelect: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching selects")(
      test("We can set a new select using its name") {
        val patch: SetProperty[PatchedSelect] = $"col".asSelect.patch.setUsingName("name")

        assertTrue(patch.value.name.contains("name") && patch.value.id.isEmpty)
      },
      test("We can set a new select using its id") {
        val patch: SetProperty[PatchedSelect] = $"col".asSelect.patch.setUsingId("id")

        assertTrue(patch.value.id.contains("id") && patch.value.name.isEmpty)
      }
    )

  def specPatchedMultiSelect: Spec[TestEnvironment with Scope, Any] = {
    def assertUpdate[E](
        patch: UpdateProperty[PatchedMultiSelect],
        initial: List[PatchedSelect],
        expected: List[PatchedSelect]
    ): TestResult = assertTrue(patch.transform(PatchedMultiSelect(initial)).map(_.multiSelect) == Right(expected))

    suite("Test patching multi selects")(
      test("We can set a new multi select") {
        val multiSelect: List[PatchedSelect] = List(PatchedSelect(None, Some("name")))

        val patch: SetProperty[PatchedMultiSelect] = $"col".asMultiSelect.patch.set(multiSelect)

        assertTrue(patch.value.multiSelect == multiSelect)
      },
      test("We can remove a select using the name") {
        val multiSelect: List[PatchedSelect] = List(PatchedSelect(None, Some("name")), PatchedSelect(None, Some("other")))

        val patch: UpdateProperty[PatchedMultiSelect] = $"col".asMultiSelect.patch.removeUsingNameIfExists("name")

        assertUpdate(patch, multiSelect, List(PatchedSelect(None, Some("other"))))
      },
      test("We can remove a select using the id") {
        val multiSelect: List[PatchedSelect] = List(PatchedSelect(Some("id"), None), PatchedSelect(Some("other"), None))

        val patch: UpdateProperty[PatchedMultiSelect] = $"col".asMultiSelect.patch.removeUsingIdIfExists("id")

        assertUpdate(patch, multiSelect, List(PatchedSelect(Some("other"), None)))
      },
      test("We can add a select using the name") {
        val patch: UpdateProperty[PatchedMultiSelect] = $"col".asMultiSelect.patch.addUsingName("name")

        assertUpdate(patch, List.empty, List(PatchedSelect(None, Some("name"))))
      },
      test("We can add a select using the id") {
        val patch: UpdateProperty[PatchedMultiSelect] = $"col".asMultiSelect.patch.addUsingId("id")

        assertUpdate(patch, List.empty, List(PatchedSelect(Some("id"), None)))
      }
    )
  }

  def specPatchedDate: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching dates")(
      test("We can set a start date") {
        val patch: SetProperty[PatchedDate] = $"col".asDate.patch.startAt(fakeDatetime)

        assertTrue(patch.value.start == fakeDatetime)
      },
      test("We can add an end date") {
        val patch: UpdateProperty[PatchedDate] = $"col".asDate.patch.endAt(fakeDatetime.plusDays(2))

        assertTrue(patch.transform(PatchedDate(fakeDatetime, None, None)).map(_.end) == Right(Some(fakeDatetime.plusDays(2))))
      },
      test("We can set a date between two dates") {
        val patch: SetProperty[PatchedDate] = $"col".asDate.patch.between(fakeDatetime, fakeDatetime.plusDays(2))

        assertTrue(patch.value.start == fakeDatetime && patch.value.end.contains(fakeDatetime.plusDays(2)))
      },
      test("We can set a start date to today") {
        val patch: UIO[SetProperty[PatchedDate]] = $"col".asDate.patch.today

        patch.map(p => assertTrue(p.value.start == OffsetDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)))
      }
    )

  def specPatchedEmail: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching emails")(
      test("We can set a new email") {
        val patch: SetProperty[PatchedEmail] = $"col".asEmail.patch.set(fakeEmail)

        assertTrue(patch.value.email == fakeEmail)
      }
    )

  def specPatchedPhoneNumber: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching phone numbers")(
      test("We can set a new phone number") {
        val patch: SetProperty[PatchedPhoneNumber] = $"col".asPhoneNumber.patch.set(fakePhoneNumber)

        assertTrue(patch.value.phoneNumber == fakePhoneNumber)
      }
    )

  def specPatchedCheckbox: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching checkbox")(
      test("We can check a checkbox") {
        val patch: SetProperty[PatchedCheckbox] = $"col".asCheckbox.patch.check

        assertTrue(patch.value.checkbox)
      },
      test("We can uncheck a checkbox") {
        val patch: SetProperty[PatchedCheckbox] = $"col".asCheckbox.patch.uncheck

        assertTrue(!patch.value.checkbox)
      },
      test("We can reverse a checkbox") {
        val patch: UpdateProperty[PatchedCheckbox] = $"col".asCheckbox.patch.reverse

        assertTrue(patch.transform(PatchedCheckbox(false)).map(_.checkbox) == Right(true))
      }
    )

  def specPatchedFiles: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching files")(
      test("We can set a list of files") {
        val files: List[Link] = List(External("name", Url(fakeUrl)))

        val patch: SetProperty[PatchedFiles] = $"col".asFiles.patch.set(files)

        assertTrue(patch.value.files == files)
      },
      test("We can set a new file") {
        val patch: UpdateProperty[PatchedFiles] = $"col".asFiles.patch.add(External("name", Url(fakeUrl)))

        assertTrue(patch.transform(PatchedFiles(Seq.empty)).map(_.files) == Right(Seq(External("name", Url(fakeUrl)))))
      },
      test("We can filter files") {
        val files: List[Link] = List(External("name", Url(fakeUrl)))

        val patch: UpdateProperty[PatchedFiles] =
          $"col".asFiles.patch.filter {
            case Link.File(name, _) => name != "name"
            case External(name, _)  => name != "name"
          }

        assertTrue(patch.transform(PatchedFiles(files)).map(_.files) == Right(Seq.empty))
      }
    )

  def specPatchedTitle: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching title")(
      test("We can set a new title") {
        val patch: SetProperty[PatchedTitle] = $"col".asTitle.patch.set("Title")

        assertTrue(patch.value.title.head.asInstanceOf[RichTextData.Text].plainText == "Title")
      },
      test("We can capitalize a title") {
        val patch: UpdateProperty[PatchedTitle] = $"col".asTitle.patch.capitalize

        val source = PatchedTitle(List(RichTextData.default("title", richtext.Annotations.default)))

        assertTrue(patch.transform(source).map(_.title.head.asInstanceOf[RichTextData.Text].plainText) == Right("Title"))
      }
    )

  def specPatchedRichText: Spec[TestEnvironment with Scope, Any] = {
    def testAnnotation(name: String, patch: UpdateProperty[PatchedRichText], expected: richtext.Annotations => Boolean) =
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

        assertTrue(
          patch.transform(default).map(_.richText.map(_.asInstanceOf[RichTextData.Text].annotations).forall(expected)) == Right(true)
        )
      }

    suite("Test patching rich text")(
      test("We can write a new rich text") {
        val patch: SetProperty[PatchedRichText] = $"col".asRichText.patch.write("A new content")

        assertTrue(patch.value.richText.headOption.map(_.asInstanceOf[RichTextData.Text].plainText).contains("A new content"))
      },
      testAnnotation("reset", $"col".asRichText.patch.reset, _ == richtext.Annotations.default),
      testAnnotation("bold", $"col".asRichText.patch.bold, _.bold),
      testAnnotation("italic", $"col".asRichText.patch.italic, _.italic),
      testAnnotation("strikethrough", $"col".asRichText.patch.strikethrough, _.strikethrough),
      testAnnotation("underline", $"col".asRichText.patch.underline, _.underline),
      testAnnotation("code", $"col".asRichText.patch.code, _.code),
      testAnnotation("color", $"col".asRichText.patch.color(Color.BlueBackground), _.color == Color.BlueBackground)
    )
  }

  def specPatchedPeople: Spec[TestEnvironment with Scope, Any] =
    suite("Test patching people")(
      test("We can set a list of people") {
        val people: List[Id] = List(Id(fakeUUID))

        val patch: SetProperty[PatchedPeople] = $"col".asPeople.patch.set(people)

        assertTrue(patch.value.people == people)
      },
      test("We can set a new person") {
        val patch: UpdateProperty[PatchedPeople] = $"col".asPeople.patch.add(Id(fakeUUID))

        assertTrue(patch.transform(PatchedPeople(Seq.empty)).map(_.people) == Right(Seq(Id(fakeUUID))))
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
        val property: PatchedDate = PatchedDate(fakeDatetime, Some(fakeDatetime.plusDays(2)), Some("America/New_York"))

        val expected: String =
          s"""{
             |  "date" : {
             |    "start" : "2022-12-24T15:10:00Z",
             |    "end" : "2022-12-26T15:10:00Z",
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
        val property: PatchedPeople = PatchedPeople(List(Id(fakeUUID)))

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
