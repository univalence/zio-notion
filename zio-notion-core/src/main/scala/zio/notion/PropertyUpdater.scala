package zio.notion

import zio.notion.model.page.PatchedProperty

sealed trait PropertyUpdater[+E, T <: PatchedProperty]

object PropertyUpdater {
  sealed trait ColumnMatcher

  object ColumnMatcher {
    final case class Predicate(f: String => Boolean) extends ColumnMatcher
    final case class One(key: String)                extends ColumnMatcher
  }

  final case class FieldSetter[P <: PatchedProperty](matcher: ColumnMatcher, value: P) extends PropertyUpdater[Nothing, P] {
    def map(f: P => P): FieldSetter[P] = FieldSetter(matcher, f(value))
  }
  final case class FieldUpdater[+E, P <: PatchedProperty](
      matcher: ColumnMatcher,
      f:       P => Either[E, P]
  ) extends PropertyUpdater[E, P] {
    def map(g: P => P): FieldUpdater[E, P] = FieldUpdater(matcher, property => f(property).map(g))
  }

  type UFieldUpdater[P <: PatchedProperty] = FieldUpdater[Nothing, P]

  object FieldUpdater {
    def succeed[E, P <: PatchedProperty](matcher: ColumnMatcher, f: P => P): FieldUpdater[E, P] =
      FieldUpdater(matcher, property => Right(f(property)))
  }
}
