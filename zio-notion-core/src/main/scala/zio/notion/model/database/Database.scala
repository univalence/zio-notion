package zio.notion.model.database

import io.circe.Encoder
import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.NotionError
import zio.notion.NotionError.PropertyNotExist
import zio.notion.PropertyUpdater.ColumnMatcher
import zio.notion.dsl.PatchedDefinition
import zio.notion.model.common.{Cover, Icon, Parent, UserId}
import zio.notion.model.common.richtext.{Annotations, RichTextData}
import zio.notion.model.database.description.PropertyDescription
import zio.notion.model.database.patch.PatchPlan
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
      title:      Option[Seq[RichTextData]],
      properties: Map[String, Option[PatchPlan]]
  ) { self =>
    def updateProperty(
        patchedDefinition: PatchedDefinition
    )(implicit manifest: Manifest[PropertyDescription.Title]): Patch = {
      val patchPlan = patchedDefinition.patchPlan

      patchedDefinition.matcher match {
        case ColumnMatcher.Predicate(f) =>
          val properties: Iterable[(String, Option[PatchPlan])] =
            database.properties.collect {
              case (key, property) if f(key) && !manifest.runtimeClass.isInstance(property) => key -> Some(patchPlan)
            }

          properties.foldLeft(self)((acc, curr) => acc.copy(properties = acc.properties + curr))
        case ColumnMatcher.One(key) =>
          database.properties.get(key) match {
            // Update an existing property
            case Some(_) => copy(properties = properties + (key -> Some(patchPlan)))
            // Create a new property
            case None =>
              val newKey: String           = patchPlan.name.getOrElse(key)
              val value: Option[PatchPlan] = Some(patchPlan.copy(name = None))

              copy(properties = properties + (newKey -> value))
          }
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
