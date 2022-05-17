package zio.notion

import io.circe._

sealed trait Removable[+T]

object Removable {
  final case class Keep[T](value: T) extends Removable[T]
  case object Remove                 extends Removable[Nothing]
  case object Ignore                 extends Removable[Nothing]

  /**
   * The encoder set different value for an Ignore json from a Remove
   * json to help the automatic magnolia derivation to get the job done.
   * During the encoding, magnolia will first filter all the nulls (from
   * None and from Ignore) and then transform empty array into null
   * values.
   */
  implicit def encoder[T: Encoder]: Encoder[Removable[T]] = {
    case Ignore      => Json.Null
    case Remove      => Json.arr()
    case Keep(value) => implicitly[Encoder[T]].apply(value)
  }
}
