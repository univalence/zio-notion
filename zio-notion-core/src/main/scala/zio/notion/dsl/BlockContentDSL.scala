package zio.notion.dsl

import zio.notion.model.block.BlockContent._
import zio.notion.model.common.enumeration.{Color, Language}
import zio.notion.model.common.richtext.RichText

trait BlockContentDSL {

  implicit class BlockContentContext(val sc: StringContext) {

    private def text(args: Any*)                           = RichText.fromString(sc.s(args: _*))
    private def code(language: Language)(args: Any*): Code = Code(text(args: _*), language)

    def h1(args: Any*): HeadingOne             = HeadingOne(text(args: _*), Color.Default)
    def h2(args: Any*): HeadingTwo             = HeadingTwo(text(args: _*), Color.Default)
    def h3(args: Any*): HeadingThree           = HeadingThree(text(args: _*), Color.Default)
    def p(args: Any*): Paragraph               = Paragraph(text(args: _*), Color.Default)
    def quote(args: Any*): Quote               = Quote(text(args: _*), Color.Default)
    def todo(args: Any*): ToDo                 = ToDo(text(args: _*), checked = false, Color.Default)
    def bulleted(args: Any*): BulletedListItem = BulletedListItem(text(args: _*), Color.Default)
    def numbered(args: Any*): NumberedListItem = NumberedListItem(text(args: _*), Color.Default)

    def abap(args: Any*): Code         = code(Language.Abap)(args: _*)
    def arduino(args: Any*): Code      = code(Language.Arduino)(args: _*)
    def bash(args: Any*): Code         = code(Language.Bash)(args: _*)
    def basic(args: Any*): Code        = code(Language.Basic)(args: _*)
    def c(args: Any*): Code            = code(Language.C)(args: _*)
    def clojure(args: Any*): Code      = code(Language.Clojure)(args: _*)
    def coffeescript(args: Any*): Code = code(Language.Coffeescript)(args: _*)
    def cplusplus(args: Any*): Code    = code(Language.Cplusplus)(args: _*)
    def csharp(args: Any*): Code       = code(Language.Csharp)(args: _*)
    def css(args: Any*): Code          = code(Language.Css)(args: _*)
    def dart(args: Any*): Code         = code(Language.Dart)(args: _*)
    def diff(args: Any*): Code         = code(Language.Diff)(args: _*)
    def docker(args: Any*): Code       = code(Language.Docker)(args: _*)
    def elixir(args: Any*): Code       = code(Language.Elixir)(args: _*)
    def elm(args: Any*): Code          = code(Language.Elm)(args: _*)
    def erlang(args: Any*): Code       = code(Language.Erlang)(args: _*)
    def flow(args: Any*): Code         = code(Language.Flow)(args: _*)
    def fortran(args: Any*): Code      = code(Language.Fortran)(args: _*)
    def fsharp(args: Any*): Code       = code(Language.Fsharp)(args: _*)
    def gherkin(args: Any*): Code      = code(Language.Gherkin)(args: _*)
    def glsl(args: Any*): Code         = code(Language.Glsl)(args: _*)
    def go(args: Any*): Code           = code(Language.Go)(args: _*)
    def graphql(args: Any*): Code      = code(Language.Graphql)(args: _*)
    def groovy(args: Any*): Code       = code(Language.Groovy)(args: _*)
    def haskell(args: Any*): Code      = code(Language.Haskell)(args: _*)
    def html(args: Any*): Code         = code(Language.Html)(args: _*)
    def java(args: Any*): Code         = code(Language.Java)(args: _*)
    def javascript(args: Any*): Code   = code(Language.Javascript)(args: _*)
    def json(args: Any*): Code         = code(Language.Json)(args: _*)
    def julia(args: Any*): Code        = code(Language.Julia)(args: _*)
    def kotlin(args: Any*): Code       = code(Language.Kotlin)(args: _*)
    def latex(args: Any*): Code        = code(Language.Latex)(args: _*)
    def less(args: Any*): Code         = code(Language.Less)(args: _*)
    def lisp(args: Any*): Code         = code(Language.Lisp)(args: _*)
    def livescript(args: Any*): Code   = code(Language.Livescript)(args: _*)
    def lua(args: Any*): Code          = code(Language.Lua)(args: _*)
    def makefile(args: Any*): Code     = code(Language.Makefile)(args: _*)
    def markdown(args: Any*): Code     = code(Language.Markdown)(args: _*)
    def markup(args: Any*): Code       = code(Language.Markup)(args: _*)
    def matlab(args: Any*): Code       = code(Language.Matlab)(args: _*)
    def mermaid(args: Any*): Code      = code(Language.Mermaid)(args: _*)
    def nix(args: Any*): Code          = code(Language.Nix)(args: _*)
    def objectiveC(args: Any*): Code   = code(Language.ObjectiveC)(args: _*)
    def ocaml(args: Any*): Code        = code(Language.Ocaml)(args: _*)
    def pascal(args: Any*): Code       = code(Language.Pascal)(args: _*)
    def perl(args: Any*): Code         = code(Language.Perl)(args: _*)
    def php(args: Any*): Code          = code(Language.Php)(args: _*)
    def plainText(args: Any*): Code    = code(Language.PlainText)(args: _*)
    def powershell(args: Any*): Code   = code(Language.Powershell)(args: _*)
    def prolog(args: Any*): Code       = code(Language.Prolog)(args: _*)
    def protobuf(args: Any*): Code     = code(Language.Protobuf)(args: _*)
    def python(args: Any*): Code       = code(Language.Python)(args: _*)
    def r(args: Any*): Code            = code(Language.R)(args: _*)
    def reason(args: Any*): Code       = code(Language.Reason)(args: _*)
    def ruby(args: Any*): Code         = code(Language.Ruby)(args: _*)
    def rust(args: Any*): Code         = code(Language.Rust)(args: _*)
    def sass(args: Any*): Code         = code(Language.Sass)(args: _*)
    def scala(args: Any*): Code        = code(Language.Scala)(args: _*)
    def scheme(args: Any*): Code       = code(Language.Scheme)(args: _*)
    def scss(args: Any*): Code         = code(Language.Scss)(args: _*)
    def shell(args: Any*): Code        = code(Language.Shell)(args: _*)
    def sql(args: Any*): Code          = code(Language.Sql)(args: _*)
    def swift(args: Any*): Code        = code(Language.Swift)(args: _*)
    def typescript(args: Any*): Code   = code(Language.Typescript)(args: _*)
    def vbDotNet(args: Any*): Code     = code(Language.VbDotNet)(args: _*)
    def verilog(args: Any*): Code      = code(Language.Verilog)(args: _*)
    def vhdl(args: Any*): Code         = code(Language.Vhdl)(args: _*)
    def visualBasic(args: Any*): Code  = code(Language.VisualBasic)(args: _*)
    def webassembly(args: Any*): Code  = code(Language.Webassembly)(args: _*)
    def xml(args: Any*): Code          = code(Language.Xml)(args: _*)
    def yaml(args: Any*): Code         = code(Language.Yaml)(args: _*)
    def javaOrCs(args: Any*): Code     = code(Language.JavaOrCs)(args: _*)
  }
}

object BlockContentDSL extends BlockContentDSL
