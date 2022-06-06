package zio.notion.model.page

import io.circe.Encoder
import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.{NotionError, Removable}
import zio.notion.NotionError._
import zio.notion.Removable.{Ignore, Keep, Remove}
import zio.notion.dsl.PageUpdateDSL._
import zio.notion.model.common.{Cover, Icon, Id, Parent}
import zio.notion.model.magnolia.PatchEncoderDerivation
import zio.notion.model.page.Page.Patch.{Operations, StatelessOperations}
import zio.notion.model.page.Page.Patch.Operations.Operation

import scala.reflect.ClassTag

import java.time.OffsetDateTime

@ConfiguredJsonCodec
final case class Page(
    createdTime:    OffsetDateTime,
    lastEditedTime: OffsetDateTime,
    createdBy:      Id,
    lastEditedBy:   Id,
    id:             String,
    cover:          Option[Cover],
    icon:           Option[Icon],
    parent:         Parent,
    archived:       Boolean,
    properties:     Map[String, Property],
    url:            String
)

// val patch =

object Page {

  final case class Patch(
      properties: Map[String, Option[PatchedProperty]],
      archived:   Option[Boolean],
      icon:       Removable[Icon],
      cover:      Removable[Cover]
  ) {
    self =>

    def setOperations(operations: StatelessOperations): Patch =
      operations.operations.foldLeft(self) { case (patch, operation) =>
        operation match {
          case Operation.Archive                     => patch.copy(archived = Some(true))
          case Operation.Unarchive                   => patch.copy(archived = Some(false))
          case Operation.RemoveIcon                  => patch.copy(icon = Remove)
          case Operation.RemoveCover                 => patch.copy(cover = Remove)
          case Operation.SetIcon(icon)               => patch.copy(icon = Keep(icon))
          case Operation.SetCover(cover)             => patch.copy(cover = Keep(cover))
          case Operation.SetProperty(colName, value) => patch.copy(properties = properties + (colName -> Some(value)))
          case Operation.RemoveProperty(key)         => patch.copy(properties = properties + (key -> None))
        }
      }

    def updateProperty[PP <: PatchedProperty](
        patch: Patch,
        page: Page,
        name: String,
        transform: PP => Either[NotionError, PP]
    )(implicit tag: ClassTag[PP]): Either[NotionError, Patch] =
      page.properties.get(name) match {
        case Some(property) if property.relatedTo[PP] =>
          patch.properties.getOrElse(name, property.toPatchedProperty) match {
            case Some(initialPatchedProperty: PP) =>
              transform(initialPatchedProperty)
                .map(patchedProperty => patch.copy(properties = patch.properties + (name -> Some(patchedProperty))))
            case None =>
              Left(PropertyIsEmpty(name))
          }
        case Some(property) =>
          // We can't update the property because it doesn't have the good type
          Left(
            PropertyWrongType(
              propertyName = name,
              expectedType = tag.runtimeClass.getSimpleName.replace("Patched", ""),
              foundType    = property.getClass.getSimpleName
            )
          )
        case None =>
          // We can't update the property because it doesn't exist
          Left(PropertyNotExist(name, page.id))
      }

    def updateOperations(page: Page)(operations: Operations): Either[NotionError, Patch] = {
      val eitherSelf: Either[NotionError, Patch] = Right(self)

      operations.operations.foldLeft(eitherSelf) { case (maybePatch, operation) =>
        maybePatch.flatMap(patch =>
          operation match {
            case stateless: Operation.Stateless => Right(patch.setOperations(stateless))
            case stateful: Operation.Stateful =>
              stateful match {
                case Operation.UpdateProperty(key, transform) => updateProperty(patch, page, key, transform)
              }
          }
        )
      }
    }
  }

  object Patch {
    val empty: Patch = Patch(Map.empty, None, Ignore, Ignore)

    final case class StatelessOperations(operations: List[Operation.Stateless]) {
      def ++(operation: Operation.Stateless): StatelessOperations = copy(operations = operations :+ operation)
    }

    final case class Operations(operations: List[Operation]) {
      def ++(operation: Operation): Operations = copy(operations = operations :+ operation)
    }

    object Operations {

      sealed trait Operation

      object Operation {

        sealed trait Stateless extends Operation { self =>
          def ++(operation: Stateless): StatelessOperations = StatelessOperations(List(self, operation))
          def ++(operation: Stateful): Operations           = Operations(List(self, operation))
        }

        sealed trait Stateful extends Operation { self =>
          def ++(operation: Operation): Operations = Operations(List(self, operation))
        }

        case object Archive                                                        extends Stateless
        case object Unarchive                                                      extends Stateless
        case object RemoveIcon                                                     extends Stateless
        case object RemoveCover                                                    extends Stateless
        final case class SetIcon(icon: Icon)                                       extends Stateless
        final case class SetCover(cover: Cover)                                    extends Stateless
        final case class SetProperty[P <: PatchedProperty](name: String, value: P) extends Stateless
        final case class RemoveProperty(name: String)                              extends Stateless

        final case class UpdateProperty[PP <: PatchedProperty](name: String, transform: PP => Either[NotionError, PP]) extends Stateful

        object UpdateProperty {

          def succeed[PP <: PatchedProperty](name: String, transform: PP => PP): UpdateProperty[PP] =
            UpdateProperty(name, pp => Right(transform(pp)))
        }
      }
    }

    implicit val encoder: Encoder[Patch] = PatchEncoderDerivation.gen[Patch]
  }
}
