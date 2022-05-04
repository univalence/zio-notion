package zio.notion.model.common.enums

import io.circe.Codec
import io.circe.generic.extras.{Configuration => CirceConfiguration}
import io.circe.generic.extras.semiauto.deriveEnumerationCodec

sealed trait RollupFunction

object RollupFunction {
  final case object Count            extends RollupFunction
  final case object CountValues      extends RollupFunction
  final case object Empty            extends RollupFunction
  final case object NotEmpty         extends RollupFunction
  final case object Unique           extends RollupFunction
  final case object ShowUnique       extends RollupFunction
  final case object PercentEmpty     extends RollupFunction
  final case object PercentNotEmpty  extends RollupFunction
  final case object Sum              extends RollupFunction
  final case object Average          extends RollupFunction
  final case object Median           extends RollupFunction
  final case object Min              extends RollupFunction
  final case object Max              extends RollupFunction
  final case object Range            extends RollupFunction
  final case object EarliestDate     extends RollupFunction
  final case object LatestDate       extends RollupFunction
  final case object DateRange        extends RollupFunction
  final case object Checked          extends RollupFunction
  final case object Unchecked        extends RollupFunction
  final case object PercentChecked   extends RollupFunction
  final case object PercentUnchecked extends RollupFunction
  final case object ShowOriginal     extends RollupFunction

  CirceConfiguration.default.withSnakeCaseConstructorNames

  implicit val codecRollupFunction: Codec[RollupFunction] = deriveEnumerationCodec[RollupFunction]
}
