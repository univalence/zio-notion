package zio.notion.model.block

import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.syntax.EncoderOps

import zio.notion.model.common
import zio.notion.model.common.Icon
import zio.notion.model.common.enumeration.{Color, Language}
import zio.notion.model.common.richtext.RichTextFragment
import zio.notion.model.magnolia.{DecoderDerivation, EncoderDerivation, NoDiscriminantNoNullEncoderDerivation}

sealed trait BlockContent

object BlockContent {
  case object Unsupported                                                                                            extends BlockContent
  case class Paragraph(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)              extends BlockContent
  case class HeadingOne(richText: Seq[RichTextFragment], color: Color)                                               extends BlockContent
  case class HeadingTwo(richText: Seq[RichTextFragment], color: Color)                                               extends BlockContent
  case class HeadingThree(richText: Seq[RichTextFragment], color: Color)                                             extends BlockContent
  case class Callout(richText: Seq[RichTextFragment], icon: Icon, color: Color, children: Seq[Block] = Seq.empty)    extends BlockContent
  case class Quote(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)                  extends BlockContent
  case class BulletedListItem(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)       extends BlockContent
  case class NumberedListItem(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)       extends BlockContent
  case class ToDo(richText: Seq[RichTextFragment], checked: Boolean, color: Color, children: Seq[Block] = Seq.empty) extends BlockContent
  case class Toggle(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)                 extends BlockContent
  case class Code(richText: Seq[RichTextFragment], language: Language)                                               extends BlockContent
  case class ChildPage(title: String)                                                                                extends BlockContent
  case class ChildDatabase(title: String)                                                                            extends BlockContent
  case class Embed(url: String)                                                                                      extends BlockContent
  case class Image(file: common.File)                                                                                extends BlockContent
  case class Video(file: common.File)                                                                                extends BlockContent
  case class File(file: common.File, caption: Seq[RichTextFragment] = Seq.empty)                                     extends BlockContent
  case class Pdf(file: common.File)                                                                                  extends BlockContent
  case class Bookmark(url: String, caption: Seq[RichTextFragment] = Seq.empty)                                       extends BlockContent
  case class Equation(expression: String)                                                                            extends BlockContent
  case object Divider                                                                                                extends BlockContent
  case class TableOfContents(color: Color)                                                                           extends BlockContent
  case object Breadcrumb                                                                                             extends BlockContent
  case class Column(children: Seq[BlockContent])                                                                     extends BlockContent
  case class ColumnList(children: Seq[Column])                                                                       extends BlockContent

  implicit val encoder: Encoder[BlockContent] = EncoderDerivation.gen[BlockContent]
  implicit val encoderImage: Encoder[Image]   = Encoder[common.File].contramap(_.file)
  implicit val encoderVideo: Encoder[Video]   = Encoder[common.File].contramap(_.file)
  implicit val encoderFile: Encoder[File]     = (a: File) => a.file.asJson.deepMerge(Json.obj("caption" -> a.caption.asJson))
  implicit val encoderPdf: Encoder[Pdf]       = Encoder[common.File].contramap(_.file)
  implicit val encoderColumn: Encoder[Column] = NoDiscriminantNoNullEncoderDerivation.gen[Column]

  implicit val decoder: Decoder[BlockContent]                     = DecoderDerivation.gen[BlockContent]
  implicit val decoderParagraph: Decoder[Paragraph]               = DecoderDerivation.gen[Paragraph]
  implicit val decoderHeadingOne: Decoder[HeadingOne]             = DecoderDerivation.gen[HeadingOne]
  implicit val decoderHeadingTwo: Decoder[HeadingTwo]             = DecoderDerivation.gen[HeadingTwo]
  implicit val decoderHeadingThree: Decoder[HeadingThree]         = DecoderDerivation.gen[HeadingThree]
  implicit val decoderCallout: Decoder[Callout]                   = DecoderDerivation.gen[Callout]
  implicit val decoderQuote: Decoder[Quote]                       = DecoderDerivation.gen[Quote]
  implicit val decoderBulletedListItem: Decoder[BulletedListItem] = DecoderDerivation.gen[BulletedListItem]
  implicit val decoderNumberedListItem: Decoder[NumberedListItem] = DecoderDerivation.gen[NumberedListItem]
  implicit val decoderToDo: Decoder[ToDo]                         = DecoderDerivation.gen[ToDo]
  implicit val decoderToggle: Decoder[Toggle]                     = DecoderDerivation.gen[Toggle]
  implicit val decoderCode: Decoder[Code]                         = DecoderDerivation.gen[Code]
  implicit val decoderChildPage: Decoder[ChildPage]               = DecoderDerivation.gen[ChildPage]
  implicit val decoderChildDatabase: Decoder[ChildDatabase]       = DecoderDerivation.gen[ChildDatabase]
  implicit val decoderEmbed: Decoder[Embed]                       = DecoderDerivation.gen[Embed]
  implicit val decoderImage: Decoder[Image]                       = Decoder[common.File].map(Image.apply)
  implicit val decoderVideo: Decoder[Video]                       = Decoder[common.File].map(Video.apply)

  implicit val decoderFile: Decoder[File] =
    (c: HCursor) =>
      for {
        file    <- c.as[common.File]
        caption <- c.downField("caption").as[Seq[RichTextFragment]]
      } yield File(file, caption)

  implicit val decoderPdf: Decoder[Pdf]                         = Decoder[common.File].map(Pdf.apply)
  implicit val decoderBookmark: Decoder[Bookmark]               = DecoderDerivation.gen[Bookmark]
  implicit val decoderEquation: Decoder[Equation]               = DecoderDerivation.gen[Equation]
  implicit val decoderTableOfContents: Decoder[TableOfContents] = DecoderDerivation.gen[TableOfContents]
  implicit val decoderColumn: Decoder[Column]                   = DecoderDerivation.gen[Column]
  implicit val decoderColumnList: Decoder[ColumnList]           = DecoderDerivation.gen[ColumnList]

}
