package zio.notion

import io.circe.Encoder

import zio.notion.model.magnolia.NoDiscriminantNoNullEncoderDerivation

/**
 * Describes the mandatory information to provide to Notion to paginate
 * the result.
 *
 * @param pageSize
 *   The max number of element to retrieve (maximum value is 100)
 * @param startCursor
 *   The start identifier to retrieve element from (None to retrieve the
 *   100 first elements)
 */
final case class Pagination(pageSize: Int, startCursor: Option[String])

object Pagination {
  def start(pageSize: Int): Pagination = Pagination(pageSize, None)
  val default: Pagination              = start(100)

  implicit val encoder: Encoder[Pagination] = NoDiscriminantNoNullEncoderDerivation.gen[Pagination]
}
