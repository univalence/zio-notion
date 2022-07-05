package zio.notion.model.common.richtext

import zio.notion.model.common.richtext.RichTextFragment.default

object RichText {
  def fromString(text: String, annotations: Annotations = Annotations.default): Seq[RichTextFragment.Text] = Seq(default(text, annotations))
}
