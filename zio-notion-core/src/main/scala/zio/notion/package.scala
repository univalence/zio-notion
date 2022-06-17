package zio

import zio.notion.NotionError.ParsingError
import zio.notion.model.page.Property
import zio.prelude.Validation

package object notion {

  def decodePropertiesAs[A](properties: Map[String, Property])(implicit A: Converter[A]): Validation[ParsingError, A] =
    A match {
      case converter: PageConverter[A] => converter.convert(properties)
      case _                           => throw new Exception("This should be unreachable, create a ticket if you see this message please.")
    }
}
