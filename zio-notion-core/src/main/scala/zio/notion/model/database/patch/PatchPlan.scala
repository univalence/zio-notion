package zio.notion.model.database.patch

import io.circe.{Encoder, Json}
import io.circe.generic.extras.Configuration.snakeCaseTransformation
import io.circe.syntax.EncoderOps

import zio.notion.model.common.enumeration.{BaseColor, RollupFunction}
import zio.notion.model.database.description.NumberDescription.NumberFormat
import zio.notion.model.database.patch.PatchPlan.PropertyType
import zio.notion.model.magnolia.NoDiscriminantNoNullEncoderDerivation

final case class PatchPlan(name: Option[String], propertyType: Option[PropertyType])

object PatchPlan {
  val unit: PatchPlan = PatchPlan(None, None)

  sealed trait PropertyType

  object PropertyType {
    final case object Title                                  extends PropertyType
    final case object RichText                               extends PropertyType
    final case class Number(format: NumberFormat)            extends PropertyType
    final case class Select(options: Seq[SelectOption])      extends PropertyType
    final case class MultiSelect(options: Seq[SelectOption]) extends PropertyType
    final case object Date                                   extends PropertyType
    final case object People                                 extends PropertyType
    final case object Files                                  extends PropertyType
    final case object Checkbox                               extends PropertyType
    final case object Url                                    extends PropertyType
    final case object Email                                  extends PropertyType
    final case object PhoneNumber                            extends PropertyType
    final case class Formula(expression: String)             extends PropertyType
    final case class Relation(databaseId: String)            extends PropertyType
    final case class Rollup(
        rollupPropertyName:   Option[String],
        relationPropertyName: Option[String],
        rollupPropertyId:     Option[String],
        relationPropertyId:   Option[String],
        function:             RollupFunction
    ) extends PropertyType
    final case object CreatedTime    extends PropertyType
    final case object CreatedBy      extends PropertyType
    final case object LastEditedTime extends PropertyType
    final case object LastEditedBy   extends PropertyType

    final case class SelectOption(name: String, color: Option[BaseColor])

    object SelectOption {
      implicit val encoder: Encoder[SelectOption] = NoDiscriminantNoNullEncoderDerivation.gen[SelectOption]
    }

    implicit val encoder: Encoder[PropertyType] = NoDiscriminantNoNullEncoderDerivation.gen[PropertyType]
  }

  implicit val encoder: Encoder[PatchPlan] =
    (patch: PatchPlan) => {
      val name: Seq[(String, Json)] = patch.name.fold(List.empty[(String, Json)])(n => List("name" -> Json.fromString(n)))
      val propertyType =
        patch.propertyType.fold(List.empty[(String, Json)]) { p =>
          val name = snakeCaseTransformation(p.getClass.getSimpleName.split('$').head)
          List(name -> p.asJson)
        }

      Json.obj(name ++ propertyType: _*)
    }
}
