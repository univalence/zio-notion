package zio.notion

import io.circe.{Error => CirceError}

sealed trait NotionError extends Throwable {
  def humanize: String
}

final case class ConnectionError(throwable: Throwable) extends NotionError {
  override def humanize: String = throwable.getMessage
}

final case class HttpError(code: Int, message: String) extends NotionError {
  override def humanize: String = s""
}

final case class JsonError(error: CirceError) extends NotionError {
  override def humanize: String = error.getMessage
}
