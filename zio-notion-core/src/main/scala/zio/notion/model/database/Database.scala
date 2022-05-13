package zio.notion.model.database

import io.circe.Encoder
import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.NotionError
import zio.notion.NotionError.PropertyNotExist
import zio.notion.PropertyUpdater.FieldMatcher
import zio.notion.model.common.{Cover, Icon, Parent, UserId}
import zio.notion.model.common.richtext.{Annotations, RichTextData}
import zio.notion.model.database.description.PropertyDescription
import zio.notion.model.database.patch.PatchedPropertyDescription
import zio.notion.model.database.patch.PatchedPropertyDescription.PatchedPropertyDescriptionMatcher
import zio.notion.model.magnolia.PatchEncoderDerivation

import java.time.OffsetDateTime

@ConfiguredJsonCodec
final case class Database(
    createdTime:    OffsetDateTime,
    lastEditedTime: OffsetDateTime,
    createdBy:      UserId,
    lastEditedBy:   UserId,
    id:             String,
    title:          List[RichTextData.Text],
    cover:          Option[Cover],
    icon:           Option[Icon],
    parent:         Parent,
    archived:       Boolean,
    properties:     Map[String, PropertyDescription],
    url:            String
) { self =>
  def patch: Database.Patch = Database.Patch(self)
}

object Database {
  final case class Patch(
      database:   Database,
      title:      Option[List[RichTextData.Text]],
      properties: Map[String, Option[PatchedPropertyDescription]]
  ) { self =>
    def updateProperty(
        propertyDescriptionMatcher: PatchedPropertyDescriptionMatcher
    )(implicit manifest: Manifest[PropertyDescription.Title]): Patch = {
      val patchDescription = propertyDescriptionMatcher.description

      propertyDescriptionMatcher.matcher match {
        case FieldMatcher.All =>
          val properties: Iterable[(String, Option[PatchedPropertyDescription])] =
            database.properties.collect {
              case (key, property) if !manifest.runtimeClass.isInstance(property) => key -> Some(patchDescription)
            }

          properties.foldLeft(self)((acc, curr) => acc.copy(properties = acc.properties + curr))
        case FieldMatcher.Predicate(f) =>
          val properties: Iterable[(String, Option[PatchedPropertyDescription])] =
            database.properties.collect {
              case (key, property) if f(key) && !manifest.runtimeClass.isInstance(property) => key -> Some(patchDescription)
            }

          properties.foldLeft(self)((acc, curr) => acc.copy(properties = acc.properties + curr))
        case FieldMatcher.One(key) =>
          database.properties.get(key) match {
            // Update an existing property
            case Some(_) => copy(properties = properties + (key -> Some(patchDescription)))
            // Create a new property
            case None =>
              val newKey: String                            = patchDescription.name.getOrElse(key)
              val value: Option[PatchedPropertyDescription] = Some(propertyDescriptionMatcher.description.copy(name = None))

              copy(properties = properties + (newKey -> value))
          }
      }
    }

    def removeProperty(key: String): Either[NotionError, Patch] =
      database.properties.get(key) match {
        case Some(_) => Right(copy(properties = properties + (key -> None)))
        case None    => Left(PropertyNotExist(key, database.id))
      }

    def updateTitle(f: List[RichTextData.Text] => List[RichTextData.Text]): Patch = copy(title = Some(f(title.getOrElse(database.title))))

    def rename(text: List[RichTextData.Text]): Patch = updateTitle(_ => text)

    def rename(text: String): Patch = rename(List(RichTextData.default(text, Annotations.default)))
  }

  object Patch {
    def apply(database: Database): Patch = Patch(database, None, Map.empty)

    implicit val encoder: Encoder[Patch] = PatchEncoderDerivation.gen[Patch]
  }
}
