package zio.notion.dsl

import zio.notion.model.common.{Cover, Icon}
import zio.notion.model.page.Page.Patch.Operations.Operation
import zio.notion.model.page.Page.Patch.Operations.Operation._

trait PageUpdateDSL {
  val archive: Operation.Stateless                              = Archive
  val unarchive: Operation.Stateless                            = Unarchive
  val removeIcon: Operation.Stateless                           = RemoveIcon
  val removeCover: Operation.Stateless                          = RemoveCover
  def setIcon(icon: Icon): Operation.Stateless                  = SetIcon(icon)
  def setCover(cover: Cover): Operation.Stateless               = SetCover(cover)
  def removeProperty(propertyName: String): Operation.Stateless = RemoveProperty(propertyName)
}

object PageUpdateDSL extends PageUpdateDSL
