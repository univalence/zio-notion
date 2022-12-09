package zio.notion.model.block

import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.syntax.EncoderOps

import zio.notion.model.block.BlockContent.LinkToPage.LinkType
import zio.notion.model.common
import zio.notion.model.common.Icon
import zio.notion.model.common.enumeration.{Color, Language}
import zio.notion.model.common.richtext.RichTextFragment
import zio.notion.model.magnolia.{DecoderDerivation, EncoderDerivation, NoDiscriminantNoNullEncoderDerivation}

sealed trait BlockContent

object BlockContent {
  // scalafmt: { maxColumn = 200 }
  final case object Unsupported                                                                                            extends BlockContent
  final case class Paragraph(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)              extends BlockContent
  final case class HeadingOne(richText: Seq[RichTextFragment], color: Color)                                               extends BlockContent
  final case class HeadingTwo(richText: Seq[RichTextFragment], color: Color)                                               extends BlockContent
  final case class HeadingThree(richText: Seq[RichTextFragment], color: Color)                                             extends BlockContent
  final case class Callout(richText: Seq[RichTextFragment], icon: Icon, color: Color, children: Seq[Block] = Seq.empty)    extends BlockContent
  final case class Quote(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)                  extends BlockContent
  final case class BulletedListItem(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)       extends BlockContent
  final case class NumberedListItem(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)       extends BlockContent
  final case class ToDo(richText: Seq[RichTextFragment], checked: Boolean, color: Color, children: Seq[Block] = Seq.empty) extends BlockContent
  final case class Toggle(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)                 extends BlockContent
  final case class Code(richText: Seq[RichTextFragment], language: Language)                                               extends BlockContent
  final case class ChildPage(title: String)                                                                                extends BlockContent
  final case class ChildDatabase(title: String)                                                                            extends BlockContent
  final case class Embed(url: String)                                                                                      extends BlockContent
  final case class Image(file: common.File)                                                                                extends BlockContent
  final case class Video(file: common.File)                                                                                extends BlockContent
  final case class File(file: common.File, caption: Seq[RichTextFragment] = Seq.empty)                                     extends BlockContent
  final case class Pdf(file: common.File)                                                                                  extends BlockContent
  final case class Bookmark(url: String, caption: Seq[RichTextFragment] = Seq.empty)                                       extends BlockContent
  final case class Equation(expression: String)                                                                            extends BlockContent
  final case object Divider                                                                                                extends BlockContent
  final case class TableOfContents(color: Color)                                                                           extends BlockContent
  final case object Breadcrumb                                                                                             extends BlockContent
  final case class Column(children: Seq[BlockContent])                                                                     extends BlockContent
  final case class ColumnList(children: Seq[Column])                                                                       extends BlockContent
  final case class LinkPreview(url: String)                                                                                extends BlockContent
  final case class Template(richText: Seq[RichTextFragment], children: Seq[BlockContent] = Seq.empty)                      extends BlockContent
  final case class LinkToPage(linkType: LinkType, id: String)                                                              extends BlockContent

  object LinkToPage {
    sealed trait LinkType

    object LinkType {
      final case object Page     extends LinkType
      final case object Database extends LinkType
    }
  }

  implicit val encoder: Encoder[BlockContent] = EncoderDerivation.gen[BlockContent].mapJson(_ deepMerge Map("object" -> "block").asJson)
  implicit val encoderColumn: Encoder[Column] = NoDiscriminantNoNullEncoderDerivation.gen[Column]
  implicit val encoderImage: Encoder[Image]   = Encoder[common.File].contramap(_.file)
  implicit val encoderVideo: Encoder[Video]   = Encoder[common.File].contramap(_.file)
  implicit val encoderPdf: Encoder[Pdf]       = Encoder[common.File].contramap(_.file)
  implicit val encoderFile: Encoder[File]     = (block: File) => block.file.asJson.deepMerge(Json.obj("caption" -> block.caption.asJson))

  implicit val encoderLinkToPage: Encoder[LinkToPage] =
    (block: LinkToPage) => {
      val key =
        block.linkType match {
          case LinkToPage.LinkType.Page     => "page_id"
          case LinkToPage.LinkType.Database => "database_id"
        }

      Json.obj("type" -> key.asJson, key -> block.id.asJson)
    }

  implicit val decoder: Decoder[BlockContent] = DecoderDerivation.gen[BlockContent]
  implicit val decoderColumn: Decoder[Column] = DecoderDerivation.gen[Column]
  implicit val decoderImage: Decoder[Image]   = Decoder[common.File].map(Image.apply)
  implicit val decoderVideo: Decoder[Video]   = Decoder[common.File].map(Video.apply)
  implicit val decoderPdf: Decoder[Pdf]       = Decoder[common.File].map(Pdf.apply)

  implicit val decoderFile: Decoder[File] =
    (c: HCursor) =>
      for {
        file    <- c.as[common.File]
        caption <- c.downField("caption").as[Seq[RichTextFragment]]
      } yield File(file, caption)

  implicit val decoderLinkToPage: Decoder[LinkToPage] =
    (c: HCursor) =>
      c.downField("page_id").as[String].map(id => LinkToPage(LinkToPage.LinkType.Page, id)) orElse
        c.downField("database_id").as[String].map(id => LinkToPage(LinkToPage.LinkType.Database, id))
  // scalafmt: { maxColumn = 140 }
}
