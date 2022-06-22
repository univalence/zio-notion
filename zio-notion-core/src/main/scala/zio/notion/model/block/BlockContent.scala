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
  case object Unsupported                                                                                               extends BlockContent
  final case class Paragraph(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)           extends BlockContent
  final case class HeadingOne(richText: Seq[RichTextFragment], color: Color)                                            extends BlockContent
  final case class HeadingTwo(richText: Seq[RichTextFragment], color: Color)                                            extends BlockContent
  final case class HeadingThree(richText: Seq[RichTextFragment], color: Color)                                          extends BlockContent
  final case class Callout(richText: Seq[RichTextFragment], icon: Icon, color: Color, children: Seq[Block] = Seq.empty) extends BlockContent
  final case class Quote(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)               extends BlockContent
  final case class BulletedListItem(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)    extends BlockContent
  final case class NumberedListItem(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty)    extends BlockContent

  final case class ToDo(richText: Seq[RichTextFragment], checked: Boolean, color: Color, children: Seq[Block] = Seq.empty)
      extends BlockContent
  final case class Toggle(richText: Seq[RichTextFragment], color: Color, children: Seq[Block] = Seq.empty) extends BlockContent
  final case class Code(richText: Seq[RichTextFragment], language: Language)                               extends BlockContent
  final case class ChildPage(title: String)                                                                extends BlockContent
  final case class ChildDatabase(title: String)                                                            extends BlockContent
  final case class Embed(url: String)                                                                      extends BlockContent
  final case class Image(file: common.File)                                                                extends BlockContent
  final case class Video(file: common.File)                                                                extends BlockContent
  final case class File(file: common.File, caption: Seq[RichTextFragment] = Seq.empty)                     extends BlockContent
  final case class Pdf(file: common.File)                                                                  extends BlockContent
  final case class Bookmark(url: String, caption: Seq[RichTextFragment] = Seq.empty)                       extends BlockContent
  final case class Equation(expression: String)                                                            extends BlockContent
  case object Divider                                                                                      extends BlockContent
  final case class TableOfContents(color: Color)                                                           extends BlockContent
  case object Breadcrumb                                                                                   extends BlockContent
  final case class Column(children: Seq[BlockContent])                                                     extends BlockContent
  final case class ColumnList(children: Seq[Column])                                                       extends BlockContent

  implicit val encoder: Encoder[BlockContent] = EncoderDerivation.gen[BlockContent]
  implicit val encoderImage: Encoder[Image]   = Encoder[common.File].contramap(_.file)
  implicit val encoderVideo: Encoder[Video]   = Encoder[common.File].contramap(_.file)
  implicit val encoderFile: Encoder[File]     = (a: File) => a.file.asJson.deepMerge(Json.obj("caption" -> a.caption.asJson))
  implicit val encoderPdf: Encoder[Pdf]       = Encoder[common.File].contramap(_.file)
  implicit val encoderColumn: Encoder[Column] = NoDiscriminantNoNullEncoderDerivation.gen[Column]

  implicit val decoder: Decoder[BlockContent] = DecoderDerivation.gen[BlockContent]
  implicit val decoderImage: Decoder[Image]   = Decoder[common.File].map(Image.apply)
  implicit val decoderVideo: Decoder[Video]   = Decoder[common.File].map(Video.apply)

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
