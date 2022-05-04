package zio.notion

import io.circe._

sealed trait Removable[+T]

object Removable {
  final case class Keep[T](value: T) extends Removable[T]
  case object Remove                 extends Removable[Nothing]
  case object Ignore                 extends Removable[Nothing]

  implicit def encoder[T: Encoder]: Encoder[Removable[T]] = {
    case Ignore      => Json.Null
    case Remove      => Json.Null
    case Keep(value) => implicitly[Encoder[T]].apply(value)
  }
}
