package example

import zio._
import zio.notion._
import zio.notion.model.common.richtext.RichTextFragment

object RetrievePage extends ZIOAppDefault {

  val notionConfiguration: NotionConfiguration =
    NotionConfiguration(
      bearer = "6A074793-D735-4BF6-9159-24351D239BBC" // Insert your own bearer
    )

  sealed trait Sex

  object Sex {
    final case object Boy   extends Sex
    final case object Girl  extends Sex
    final case object Other extends Sex

    implicit val converter: Converter[Sex] =
      Converter.convertEnumeration {
        case "boy"   => Boy
        case "girl"  => Girl
        case "other" => Other
      }
  }

  final case class Row(title: Seq[RichTextFragment], @NotionColumn("Age") age: Int, sex: Sex)

  def example: ZIO[Notion, NotionError, Unit] =
    for {
      page <- Notion.retrievePage("6A074793-D735-4BF6-9159-24351D239BBC")
      row  <- page.propertiesAs[Row].toZIO
      _    <- Console.printLine(row).orDie
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    example.provide(Notion.layerWith("6A074793-D735-4BF6-9159-24351D239BBC")) // Insert your own bearer
}
