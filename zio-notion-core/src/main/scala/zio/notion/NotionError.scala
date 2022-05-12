package zio.notion

import io.circe.{Error => CirceError}

sealed trait NotionError extends Throwable {
  def humanize: String
}

object NotionError {
  final case class ConnectionError(throwable: Throwable) extends NotionError {
    override def humanize: String = throwable.getMessage
  }

  final case class HttpError(curl: String, status: Int, code: String, message: String) extends NotionError {
    override def humanize: String =
      s"""$message
         |
         |The following request to Notion failed with a status $status corresponding to "$code":
         |${curl.split("\n").map(" > " + _).mkString("\n")}""".stripMargin
  }

  final case class JsonError(error: CirceError) extends NotionError {
    override def humanize: String = error.getMessage
  }

  final case class PropertyNotExist(propertyName: String, id: String) extends NotionError {
    override def humanize: String = s"Property $propertyName doesn't exists for $id."
  }

  final case class PropertyWrongType(propertyName: String, expectedType: String, foundType: String) extends NotionError {
    override def humanize: String = s"Property named $propertyName can't be updated, expected a $expectedType but found a $foundType."
  }

  final case class PropertyIsEmpty(propertyName: String) extends NotionError {
    override def humanize: String = s"Property $propertyName can't be updated because is is empty."
  }
}
