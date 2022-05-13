package zio.notion.model.page

import io.circe.Encoder
import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.{NotionError, Patchable, PropertyUpdater, Removable}
import zio.notion.NotionError._
import zio.notion.PropertyUpdater.FieldMatcher
import zio.notion.Removable.{Ignore, Keep, Remove}
import zio.notion.model.common.{Cover, Icon, Parent, UserId}
import zio.notion.model.magnolia.PatchEncoderDerivation
import zio.notion.model.page.patch.PatchedProperty
import zio.notion.model.page.property.Property

import scala.reflect.ClassTag

import java.time.OffsetDateTime

@ConfiguredJsonCodec
final case class Page(
    createdTime:    OffsetDateTime,
    lastEditedTime: OffsetDateTime,
    createdBy:      UserId,
    lastEditedBy:   UserId,
    id:             String,
    cover:          Option[Cover],
    icon:           Option[Icon],
    parent:         Parent,
    archived:       Boolean,
    properties:     Map[String, Property],
    url:            String
) { self =>
  def patch: Page.Patch = Page.Patch(self)
}

object Page {
  final case class Patch(
      page:       Page,
      properties: Map[String, Option[PatchedProperty]],
      archived:   Option[Boolean],
      icon:       Removable[Icon],
      cover:      Removable[Cover]
  ) { self =>

    /**
     * Create a patch to update a property of the page.
     *
     * @param updater
     *   The transformation description for the properties
     * @param patchable
     *   The getter from a retrieved property to a patched one
     * @param tag
     *   The ClassTag that helps us getting the class name
     * @param manifest
     *   The Manifest that helps us getting the instance of a parametric
     *   subtype
     * @tparam E
     *   The error
     * @tparam I
     *   The input property
     * @tparam O
     *   The output patched property
     * @return
     */
    def updateProperty[E >: NotionError, I <: Property, O <: PatchedProperty](
        updater: PropertyUpdater[E, O]
    )(implicit patchable: Patchable[I, O], tag: ClassTag[O], manifest: Manifest[I]): Either[E, Patch] = {
      val default: Either[E, Patch] = Right(self)

      def updateProperties(
          predicate: String => Boolean,
          update: (String, Option[O]) => Either[E, (String, Option[O])]
      ): Either[E, Patch] = {
        val maybeProperties: Iterable[Either[E, (String, Option[O])]] =
          page.properties.collect {
            case (key, property: I) if predicate(key) && manifest.runtimeClass.isInstance(property) =>
              update(key, properties.getOrElse(key, patchable.patch(property)).map(_.asInstanceOf[O]))
          }

        maybeProperties.foldLeft(default)((acc, curr) =>
          acc.flatMap(patch => curr.map(property => patch.copy(properties = patch.properties + property)))
        )
      }

      def setProperties(predicate: String => Boolean, value: O): Either[E, Patch] =
        updateProperties(predicate, (key, _) => Right(key -> Some(value)))

      def transformProperties(predicate: String => Boolean, transform: O => Either[E, O]): Either[E, Patch] =
        updateProperties(
          predicate,
          (key, maybeInput) =>
            maybeInput match {
              case Some(input) => transform(input).map(key -> Some(_))
              case None        => Left(PropertyIsEmpty(key))
            }
        )

      def updateOneProperty(key: String, update: Option[O] => Either[E, O]): Either[E, Patch] =
        page.properties.get(key) match {
          case Some(property: I) if manifest.runtimeClass.isInstance(property) =>
            update(
              properties
                .getOrElse(key, patchable.patch(property))
                .map(_.asInstanceOf[O])
            )
              .map(value => copy(properties = properties + (key -> Some(value))))
          case Some(property) =>
            // We can't update the property because it doesn't have the good type
            Left(PropertyWrongType(key, manifest.runtimeClass.getSimpleName, property.getClass.getSimpleName))
          case None =>
            // We can't update the property because it doesn't exist
            Left(PropertyNotExist(key, page.id))
        }

      updater match {
        case PropertyUpdater.FieldSetter(matcher, value) =>
          matcher match {
            case FieldMatcher.All          => setProperties(_ => true, value)
            case FieldMatcher.Predicate(f) => setProperties(f, value)
            case FieldMatcher.One(key)     => updateOneProperty(key, _ => Right(value))
          }
        case PropertyUpdater.FieldUpdater(matcher, transform) =>
          matcher match {
            case FieldMatcher.All          => transformProperties(_ => true, transform)
            case FieldMatcher.Predicate(f) => transformProperties(f, transform)
            case FieldMatcher.One(key) =>
              updateOneProperty(
                key,
                {
                  case Some(input) => transform(input)
                  // We can't update the property has no value
                  case None => Left(PropertyIsEmpty(key))
                }
              )
          }
      }
    }

    def removeProperty(key: String): Patch = copy(properties = properties + (key -> None))

    def archive: Patch = copy(archived = Some(true))

    def unarchive: Patch = copy(archived = Some(false))

    def updateIcon(icon: Icon): Patch = copy(icon = Keep(icon))

    def removeIcon: Patch = copy(icon = Remove)

    def updateCover(cover: Cover): Patch = copy(cover = Keep(cover))

    def removeCover: Patch = copy(cover = Remove)
  }

  object Patch {
    def apply(page: Page): Patch = Patch(page, Map.empty, None, Ignore, Ignore)

    implicit val encoder: Encoder[Patch] = PatchEncoderDerivation.gen[Patch]
  }
}
