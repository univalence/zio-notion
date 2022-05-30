package zio.notion.model.database.query

import io.circe.Encoder

import zio.notion.model.magnolia.NoDiscriminantNoNullEncoderDerivation

final case class Query(filter: Option[Filter], sorts: Option[Sorts])

object Query {
  val empty: Query = Query(None, None)

  implicit val encoder: Encoder[Query] = NoDiscriminantNoNullEncoderDerivation.gen[Query] // deriveEncoder[Query]
}
