package zio.notion.dsl

trait ColumnDSL {

  implicit class ColumnContext(val sc: StringContext) {
    private def text(args: Any*): String = sc.s(args: _*)

    def $(args: Any*): Column = col(text(args: _*))

    def $$(args: Any*): ColumnDefinition = colDefinition(text(args: _*))
  }

  def col(colName: String): Column = Column(colName)

  def colDefinition(colName: String): ColumnDefinition = ColumnDefinition(colName)
}

object ColumnDSL extends ColumnDSL
