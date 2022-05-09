package zio.notion

import zio.notion.model.page.patch.PatchedProperty

sealed trait PropertyUpdater[+E, T <: PatchedProperty]

object PropertyUpdater {
  sealed trait FieldMatcher

  object FieldMatcher {
    final case object All                            extends FieldMatcher
    final case class Predicate(f: String => Boolean) extends FieldMatcher
    final case class One(key: String)                extends FieldMatcher
  }

  final case class FieldSetter[T <: PatchedProperty](matcher: FieldMatcher, value: T) extends PropertyUpdater[Nothing, T]
  final case class FieldUpdater[+E, T <: PatchedProperty](matcher: FieldMatcher, transform: T => Either[E, T]) extends PropertyUpdater[E, T]

  type UTransformation[P <: PatchedProperty] = Transformation[Nothing, P]

  trait Patch[E, P <: PatchedProperty]

  trait Transformation[E, P <: PatchedProperty] extends Patch[E, P] {
    def transform(property: P): Either[E, P]

    def andThen(next: Transformation[E, P]): Transformation[E, P] = date => transform(date).flatMap(curr => next.transform(curr))

    def on(fieldName: String): FieldUpdater[E, P] = FieldUpdater(FieldMatcher.One(fieldName), transform)

    def onAll: FieldUpdater[E, P] = FieldUpdater(FieldMatcher.All, transform)

    def onAllMatching(predicate: String => Boolean): FieldUpdater[E, P] = FieldUpdater(FieldMatcher.Predicate(predicate), transform)
  }

  object Transformation {
    def apply[E, P <: PatchedProperty](f: P => Either[E, P]): Transformation[E, P] = (property: P) => f(property)

    def succeed[P <: PatchedProperty](f: P => P): UTransformation[P] = Transformation(p => Right(f(p)))
  }

  trait Setter[P <: PatchedProperty] extends Patch[Nothing, P] {
    protected def build(): P
    final def value: P = build()

    final def on(fieldName: String): FieldSetter[P] = FieldSetter(FieldMatcher.One(fieldName), value)

    def onAll: FieldSetter[P] = FieldSetter(FieldMatcher.All, value)

    def onAllMatching(predicate: String => Boolean): FieldSetter[P] = FieldSetter(FieldMatcher.Predicate(predicate), value)
  }

  object Setter {
    def apply[P <: PatchedProperty](p: P): Setter[P] = () => p
  }
}
