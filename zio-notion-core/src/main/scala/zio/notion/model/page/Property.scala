package zio.notion.model.page

import io.circe.generic.extras._

import zio.notion.model.common.Id
import zio.notion.model.common.richtext.RichTextData
import zio.notion.model.page.PatchedProperty._
import zio.notion.model.page.property.Link
import zio.notion.model.page.property.data.{DateData, FormulaData, RollupData, SelectData}

import scala.reflect.ClassTag

@ConfiguredJsonCodec sealed trait Property {
  def id: String

  type PP <: PatchedProperty
  def toPatchedProperty: Option[PP]
  def relatedTo[PP](implicit tag: ClassTag[PP]): Boolean = tag.runtimeClass.isInstanceOf[PP]
}

object Property {

  final case class Number(id: String, number: Option[Double]) extends Property {
    override type PP = PatchedNumber
    override def toPatchedProperty: Option[PatchedNumber] = number.map(PatchedNumber.apply)
  }

  final case class Url(id: String, url: Option[String]) extends Property {
    override type PP = PatchedUrl
    override def toPatchedProperty: Option[PatchedUrl] = url.map(PatchedUrl.apply)
  }

  // We can't update a select from an existing one.
  final case class Select(id: String, select: Option[SelectData]) extends Property {
    override type PP = PatchedSelect
    override def toPatchedProperty: Option[PatchedSelect] = None
  }

  final case class MultiSelect(id: String, multiSelect: List[SelectData]) extends Property {
    override type PP = PatchedMultiSelect

    override def toPatchedProperty: Option[PatchedMultiSelect] =
      Some(PatchedMultiSelect(multiSelect.map(data => PatchedSelect(Some(data.id), Some(data.name)))))
  }

  final case class Date(id: String, date: Option[DateData]) extends Property {
    override type PP = PatchedDate
    override def toPatchedProperty: Option[PatchedDate] = date.map(date => PatchedDate(date.start, date.end, date.timeZone))
  }

  final case class Email(id: String, email: Option[String]) extends Property {
    override type PP = PatchedEmail
    override def toPatchedProperty: Option[PatchedEmail] = email.map(PatchedEmail.apply)
  }

  final case class PhoneNumber(id: String, phoneNumber: Option[String]) extends Property {
    override type PP = PatchedPhoneNumber
    override def toPatchedProperty: Option[PatchedPhoneNumber] = phoneNumber.map(PatchedPhoneNumber.apply)
  }

  final case class Checkbox(id: String, checkbox: Option[Boolean]) extends Property {
    override type PP = PatchedCheckbox
    override def toPatchedProperty: Option[PatchedCheckbox] = checkbox.map(PatchedCheckbox.apply)
  }

  final case class Files(id: String, files: Seq[Link]) extends Property {
    override type PP = PatchedFiles
    override def toPatchedProperty: Option[PatchedFiles] = Some(PatchedFiles(files))
  }

  final case class Title(id: String, title: Seq[RichTextData]) extends Property {
    override type PP = PatchedTitle
    override def toPatchedProperty: Option[PatchedTitle] = Some(PatchedTitle(title))
  }

  final case class RichText(id: String, richText: Seq[RichTextData]) extends Property {
    override type PP = PatchedRichText
    override def toPatchedProperty: Option[PatchedRichText] = Some(PatchedRichText(richText))
  }

  final case class People(id: String, people: Seq[Id]) extends Property {
    override type PP = PatchedPeople
    override def toPatchedProperty: Option[PatchedPeople] = Some(PatchedPeople(people))
  }

  final case class Relation(id: String, relation: Seq[Id]) extends Property {
    override type PP = PatchedRelation
    override def toPatchedProperty: Option[PatchedRelation] = Some(PatchedRelation(relation))
  }

  final case class CreatedBy(id: String, createdBy: Id) extends Property {
    override type PP = PatchedProperty
    override def toPatchedProperty: Option[PatchedProperty]         = None
    override def relatedTo[PP](implicit tag: ClassTag[PP]): Boolean = false
  }

  final case class CreatedTime(id: String, createdTime: String) extends Property {
    override type PP = PatchedProperty
    override def toPatchedProperty: Option[PatchedProperty]         = None
    override def relatedTo[PP](implicit tag: ClassTag[PP]): Boolean = false
  }

  final case class LastEditedBy(id: String, lastEditedBy: Id) extends Property {
    override type PP = PatchedProperty
    override def toPatchedProperty: Option[PatchedProperty]         = None
    override def relatedTo[PP](implicit tag: ClassTag[PP]): Boolean = false
  }

  final case class LastEditedTime(id: String, lastEditedTime: String) extends Property {
    override type PP = PatchedProperty
    override def toPatchedProperty: Option[PatchedProperty]         = None
    override def relatedTo[PP](implicit tag: ClassTag[PP]): Boolean = false
  }

  final case class Formula(id: String, formula: FormulaData) extends Property {
    // TODO: The patched version should be implemented
    override type PP = PatchedProperty
    override def toPatchedProperty: Option[PatchedProperty]         = None
    override def relatedTo[PP](implicit tag: ClassTag[PP]): Boolean = false
  }

  final case class Rollup(id: String, rollup: RollupData) extends Property {
    // TODO: The patched version should be implemented
    override type PP = PatchedProperty
    override def toPatchedProperty: Option[PatchedProperty]         = None
    override def relatedTo[PP](implicit tag: ClassTag[PP]): Boolean = false
  }
}
