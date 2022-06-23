package zio.notion.utils

import io.circe.generic.extras.Configuration.snakeCaseTransformation

object StringOps {

  val digitify: String => String =
    (string: String) => {
      val notSurroundedByCharacters: String => String = "(?<=[^a-zA-Z0-9])" + _ + "(?![a-zA-Z0-9])"

      val replacements =
        Seq(
          "one"   -> "1",
          "two"   -> "2",
          "three" -> "3"
        )

      replacements.foldLeft(string) { case (acc, (from, to)) => acc.replaceAll(notSurroundedByCharacters(from), to) }
    }

  val notionify: String => String = snakeCaseTransformation.andThen(digitify)
}
