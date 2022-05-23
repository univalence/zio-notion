package zio.notion

import zio.notion.model.page.patch.PatchedProperty

sealed trait PropertyUpdater[+E, T <: PatchedProperty]

object PropertyUpdater {
  sealed trait ColumnMatcher

  object ColumnMatcher {
    final case class Predicate(f: String => Boolean) extends ColumnMatcher
    final case class One(key: String)                extends ColumnMatcher
  }

  final case class FieldSetter[T <: PatchedProperty](matcher: ColumnMatcher, value: T) extends PropertyUpdater[Nothing, T]
  final case class FieldUpdater[+E, T <: PatchedProperty](matcher: ColumnMatcher, transform: T => Either[E, T])
      extends PropertyUpdater[E, T]

  type UTransformation[P <: PatchedProperty] = Transformation[Nothing, P]

  trait Patch[E, P <: PatchedProperty]

  trait Transformation[E, P <: PatchedProperty] extends Patch[E, P] { self =>
    def transform(property: P): Either[E, P]

    def map(f: P => P): Transformation[E, P] = Transformation(p => transform(p).map(f))

    def andThen(next: Transformation[E, P]): Transformation[E, P] = date => transform(date).flatMap(curr => next.transform(curr))

    def on(fieldName: String): FieldUpdater[E, P] = FieldUpdater(ColumnMatcher.One(fieldName), transform)

    def onAll: FieldUpdater[E, P] = FieldUpdater(ColumnMatcher.Predicate(_ => true), transform)

    def onAllMatching(predicate: String => Boolean): FieldUpdater[E, P] = FieldUpdater(ColumnMatcher.Predicate(predicate), transform)
  }

  object Transformation {
    def apply[E, P <: PatchedProperty](f: P => Either[E, P]): Transformation[E, P] = (property: P) => f(property)

    def succeed[P <: PatchedProperty](f: P => P): UTransformation[P] = Transformation(p => Right(f(p)))
  }

  trait Setter[P <: PatchedProperty] extends Patch[Nothing, P] {
    protected def build(): P
    final def value: P = build()

    def map(f: P => P): Setter[P] = Setter(f(value))

    final def on(fieldName: String): FieldSetter[P] = FieldSetter(ColumnMatcher.One(fieldName), value)

    def onAll: FieldSetter[P] = FieldSetter(ColumnMatcher.Predicate(_ => true), value)

    def onAllMatching(predicate: String => Boolean): FieldSetter[P] = FieldSetter(ColumnMatcher.Predicate(predicate), value)
  }

  object Setter {
    def apply[P <: PatchedProperty](p: P): Setter[P] = () => p
  }
}
