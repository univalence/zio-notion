package zio.notion.model

import io.circe.generic.extras._

@ConfiguredJsonCodec sealed trait RichTextData

final case class Text(text: TextData, annotations: Annotations, plainText: String, href: Option[String])
    extends RichTextData

final case class Mention(mention: MentionData, annotations: Annotations, plainText: String, href: Option[String])
    extends RichTextData

final case class Equation(expression: Expression, annotations: Annotations, plainText: String, href: Option[String])
    extends RichTextData
