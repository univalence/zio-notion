package zio.notion

import zio.ZIO
import zio.notion.model.Property

sealed trait PropertyUpdater[-R, +E, T <: Property]

object PropertyUpdater {
  final case class OneFieldUpdater[R, +E, T <: Property](fieldName: String, f: T => ZIO[R, E, T])                     extends PropertyUpdater[R, E, T]
  final case class AllFieldsUpdater[R, +E, T <: Property](f: T => ZIO[R, E, T])                                       extends PropertyUpdater[R, E, T]
  final case class AllFieldsPredicateUpdater[R, E, T <: Property](predicate: String => Boolean, f: T => ZIO[R, E, T]) extends PropertyUpdater[R, E, T]

  trait Transformation[R, E, P <: Property] {
    def transform(property: P): ZIO[R, E, P]

    def andThen(next: Transformation[R, E, P]): Transformation[R, E, P] = date => transform(date).flatMap(curr => next.transform(curr))

    def on(fieldName: String): OneFieldUpdater[R, E, P] = OneFieldUpdater(fieldName, transform)
  }
}
