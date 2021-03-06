package zio.notion.model.page

import io.circe.Encoder
import io.circe.generic.extras.ConfiguredJsonCodec

import zio.notion.{decodePropertiesAs, Converter, NotionError, Removable}
import zio.notion.NotionError.ParsingError
import zio.notion.Removable.{Ignore, Keep, Remove}
import zio.notion.model.common.{Cover, Icon, Id, Parent}
import zio.notion.model.magnolia.PatchEncoderDerivation
import zio.notion.model.page.Page.Patch.{Operations, StatelessOperations}
import zio.notion.model.page.Page.Patch.Operations.Operation
import zio.notion.model.page.Page.Patch.Operations.Operation.UpdateProperty.Transform.{Compute, IgnoreEmpty}
import zio.prelude.Validation

import scala.reflect.ClassTag

import java.time.OffsetDateTime

@ConfiguredJsonCodec(decodeOnly = true)
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
) {

  /**
   * Convert the properties of a Page into a particular case class.
   *
   * It uses Magnolia to automatically implement a converter for your
   * case class and it supports optional values.
   */
  def propertiesAs[A](implicit A: Converter[A]): Validation[ParsingError, A] = decodePropertiesAs[A](properties)
}

object Page {

  final case class Patch(
      properties: Map[String, Option[PatchedProperty]],
      archived:   Option[Boolean],
      icon:       Removable[Icon],
      cover:      Removable[Cover]
  ) {
    self =>

    def setOperation(operation: Operation.Stateless): Patch =
      operation match {
        case Operation.Archive                     => copy(archived = Some(true))
        case Operation.Unarchive                   => copy(archived = Some(false))
        case Operation.RemoveIcon                  => copy(icon = Remove)
        case Operation.RemoveCover                 => copy(cover = Remove)
        case Operation.SetIcon(icon)               => copy(icon = Keep(icon))
        case Operation.SetCover(cover)             => copy(cover = Keep(cover))
        case Operation.SetProperty(colName, value) => copy(properties = properties + (colName -> Some(value)))
        case Operation.RemoveProperty(key)         => copy(properties = properties + (key -> None))
      }

    def setOperations(operations: StatelessOperations): Patch =
      operations.operations.foldLeft(self) { case (patch, operation) => patch.setOperation(operation) }

    def updateOperation(page: Page, operation: Operation): Either[NotionError, Patch] =
      operation match {
        case stateless: Operation.Stateless => Right(setOperation(stateless))
        case stateful: Operation.Stateful =>
          stateful match {
            case op: Operation.UpdateProperty => op.updatePatch(page, self)
          }
      }

    def updateOperations(page: Page, operations: Operations): Either[NotionError, Patch] = {
      val eitherSelf: Either[NotionError, Patch] = Right(self)

      operations.operations.foldLeft(eitherSelf) { case (maybePatch, operation) =>
        maybePatch.flatMap(patch => patch.updateOperation(page, operation))
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

        case object Archive                                                extends Stateless
        case object Unarchive                                              extends Stateless
        case object RemoveIcon                                             extends Stateless
        case object RemoveCover                                            extends Stateless
        final case class SetIcon(icon: Icon)                               extends Stateless
        final case class SetCover(cover: Cover)                            extends Stateless
        final case class RemoveProperty(name: String)                      extends Stateless
        final case class SetProperty(name: String, value: PatchedProperty) extends Stateless

        final case class UpdateProperty(name: String, transform: UpdateProperty.Transform) extends Stateful {

          def updatePatch(page: Page, patch: Patch): Either[NotionError, Patch] = {
            val patchedProperty: Option[PatchedProperty] =
              patch.properties.getOrElse(name, page.properties.get(name).flatMap(ToPatchedProperty.apply))

            transform.lift(name)(patchedProperty) match {
              case Left(value) => Left(value)
              case Right(value) =>
                value match {
                  case Some(value) => Right(patch.copy(properties = patch.properties + (name -> Some(value))))
                  case None        => Right(patch)
                }
            }
          }

          def ignoreEmpty: UpdateProperty = copy(transform = UpdateProperty.Transform.IgnoreEmpty(transform))
        }

        object UpdateProperty {

          sealed trait Transform { self =>
            def lift(name: String): Option[PatchedProperty] => Either[NotionError, Option[PatchedProperty]]

            def ignoreEmpty: Transform = IgnoreEmpty(self)
          }

          object Transform {

            /** Describe a property computation. */
            final case class Compute[PP <: PatchedProperty: ClassTag](transform: PP => Either[NotionError, PP]) extends Transform {

              override def lift(name: String): Option[PatchedProperty] => Either[NotionError, Option[PatchedProperty]] = {
                case Some(pp: PP) => transform(pp).map(Some(_))
                case Some(pp) =>
                  Left(
                    NotionError.PropertyWrongType(
                      name,
                      implicitly[ClassTag[PP]].runtimeClass.getSimpleName.replace("Patched", ""),
                      pp.getClass.getSimpleName.replace("Patched", "")
                    )
                  )
                case None => Left(NotionError.PropertyIsEmpty(name))
              }
            }

            /** Ignore the empty case of a computation. */
            final case class IgnoreEmpty(transform: Transform) extends Transform {

              override def lift(name: String): Option[PatchedProperty] => Either[NotionError, Option[PatchedProperty]] = {
                case None => Right(None)
                case x    => transform.lift(name)(x)
              }
            }
          }

          def succeed[PP <: PatchedProperty: ClassTag](name: String, transform: PP => PP): UpdateProperty =
            UpdateProperty(name, Compute[PP](pp => Right(transform(pp))))

          def attempt[PP <: PatchedProperty: ClassTag](name: String, transform: PP => Either[NotionError, PP]): UpdateProperty =
            UpdateProperty(name, Compute[PP](pp => transform(pp)))
        }
      }
    }

    implicit val encoder: Encoder[Patch] = PatchEncoderDerivation.gen[Patch]
  }
}
