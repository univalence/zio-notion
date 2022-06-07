package zio.notion.dsl

import zio.notion.model.common.{Cover, Icon}
import zio.notion.model.page.Page.Patch.{Operations, StatelessOperations}
import zio.notion.model.page.Page.Patch.Operations.Operation
import zio.notion.model.page.Page.Patch.Operations.Operation._

trait PageUpdateDSL {
  implicit def statefulToOperations(operation: Operation.Stateful): Operations   = Operations(List(operation))
  implicit def statelessToOperations(operation: Operation.Stateless): Operations = Operations(List(operation))

  implicit def statelessToStatelessOperations(operation: Operation.Stateless): StatelessOperations = StatelessOperations(List(operation))

  val archive: Operation.Stateless                              = Archive
  val unarchive: Operation.Stateless                            = Unarchive
  val removeIcon: Operation.Stateless                           = RemoveIcon
  val removeCover: Operation.Stateless                          = RemoveCover
  def setIcon(icon: Icon): Operation.Stateless                  = SetIcon(icon)
  def setCover(cover: Cover): Operation.Stateless               = SetCover(cover)
  def removeProperty(propertyName: String): Operation.Stateless = RemoveProperty(propertyName)
}

object PageUpdateDSL extends PageUpdateDSL
