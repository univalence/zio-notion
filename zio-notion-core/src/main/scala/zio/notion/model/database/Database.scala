package zio.notion.model.database

import io.circe.Encoder
import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.NotionError
import zio.notion.NotionError.PropertyNotExist
import zio.notion.dsl.PatchedColumnDefinition
import zio.notion.model.common.{Cover, Icon, Id, Parent}
import zio.notion.model.common.richtext.{Annotations, RichTextData}
import zio.notion.model.magnolia.PatchEncoderDerivation

import java.time.OffsetDateTime

@ConfiguredJsonCodec
final case class Database(
    createdTime:    OffsetDateTime,
    lastEditedTime: OffsetDateTime,
    createdBy:      Id,
    lastEditedBy:   Id,
    id:             String,
    title:          Seq[RichTextData],
    cover:          Option[Cover],
    icon:           Option[Icon],
    parent:         Parent,
    archived:       Boolean,
    properties:     Map[String, PropertyDefinition],
    url:            String
) { self =>
  def patch: Database.Patch = Database.Patch(self)
}

object Database {

  final case class Patch(
      database:   Database,
      title:      Option[Seq[RichTextData]],
      properties: Map[String, Option[PatchedPropertyDefinition]]
  ) { self =>

    def updateProperty(patchedColumnDefinition: PatchedColumnDefinition): Patch = {
      val name  = patchedColumnDefinition.columnName
      val patch = patchedColumnDefinition.patch

      database.properties.get(name) match {
        // Update an existing property
        case Some(_) => copy(properties = properties + (name -> Some(patch)))
        // Create a new property
        case None =>
          val newName: String                          = patch.name.getOrElse(name)
          val value: Option[PatchedPropertyDefinition] = Some(patch.copy(name = None))

          copy(properties = properties + (newName -> value))
      }
    }

    def removeProperty(key: String): Either[NotionError, Patch] =
      database.properties.get(key) match {
        case Some(_) => Right(copy(properties = properties + (key -> None)))
        case None    => Left(PropertyNotExist(key, database.id))
      }

    def updateTitle(f: Seq[RichTextData] => Seq[RichTextData]): Patch = copy(title = Some(f(title.getOrElse(database.title))))

    def rename(text: Seq[RichTextData.Text]): Patch = updateTitle(_ => text)

    def rename(text: String): Patch = rename(Seq(RichTextData.default(text, Annotations.default)))
  }

  object Patch {
    def apply(database: Database): Patch = Patch(database, None, Map.empty)

    implicit val encoder: Encoder[Patch] = PatchEncoderDerivation.gen[Patch]
  }
}
