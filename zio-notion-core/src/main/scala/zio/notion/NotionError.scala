package zio.notion

import io.circe.{Error => CirceError}

import java.util.UUID

sealed trait NotionError extends Throwable {
  def humanize: String
}

final case class ConnectionError(throwable: Throwable) extends NotionError {
  override def humanize: String = throwable.getMessage
}

final case class HttpError(code: Int, message: String) extends NotionError {
  override def humanize: String = s"$code: $message"
}

final case class JsonError(error: CirceError) extends NotionError {
  override def humanize: String = error.getMessage
}

final case class PropertyNotExist(propertyName: String, pageId: UUID) extends NotionError {
  override def humanize: String = s"Property $propertyName doesn't exists for $pageId."
}

final case class PropertyWrongType(propertyName: String, expectedType: String) extends NotionError {
  override def humanize: String = s"Property $propertyName can't be updated, expected $expectedType type."
}

final case class PropertyIsEmpty(propertyName: String) extends NotionError {
  override def humanize: String = s"Property $propertyName can't be updated because is is empty."
}
