package zio.notion.model

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec sealed trait NumberFormat
object NumberDescription {
  @ConfiguredJsonCodec final case class NumberDescription(format: NumberFormat)

  final case object Number           extends NumberFormat
  final case object NumberWithCommas extends NumberFormat
  final case object Percent          extends NumberFormat
  final case object Dollar           extends NumberFormat
  final case object CanadianDollar   extends NumberFormat
  final case object Euro             extends NumberFormat
  final case object Pound            extends NumberFormat
  final case object Yen              extends NumberFormat
  final case object Ruble            extends NumberFormat
  final case object Rupee            extends NumberFormat
  final case object Won              extends NumberFormat
  final case object Yuan             extends NumberFormat
  final case object Real             extends NumberFormat
  final case object Lira             extends NumberFormat
  final case object Rupiah           extends NumberFormat
  final case object Franc            extends NumberFormat
  final case object HongKongDollar   extends NumberFormat
  final case object NewZealandDollar extends NumberFormat
  final case object Krona            extends NumberFormat
  final case object NorwegianKrone   extends NumberFormat
  final case object MexicanPeso      extends NumberFormat
  final case object Rand             extends NumberFormat
  final case object NewTaiwanDollar  extends NumberFormat
  final case object DanishKrone      extends NumberFormat
  final case object Zloty            extends NumberFormat
  final case object Baht             extends NumberFormat
  final case object Forint           extends NumberFormat
  final case object Koruna           extends NumberFormat
  final case object Shekel           extends NumberFormat
  final case object ChileanPeso      extends NumberFormat
  final case object PhilippinePeso   extends NumberFormat
  final case object Dirham           extends NumberFormat
  final case object ColombianPeso    extends NumberFormat
  final case object Riyal            extends NumberFormat
  final case object Ringgit          extends NumberFormat
  final case object Leu              extends NumberFormat
  final case object ArgentinePeso    extends NumberFormat
  final case object UruguayanPeso    extends NumberFormat

}
