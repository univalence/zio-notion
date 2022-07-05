package zio.notion.dsl

import zio._
import zio.notion.model.block.BlockContent.Code
import zio.notion.model.common.enumeration.Language
import zio.notion.model.common.richtext.RichText
import zio.test._

object BlockContentDSLSpec extends ZIOSpecDefault {

  def codeTest(language: Language, interpolation: BlockContentContext => Seq[Any] => Code): Spec[Any, Nothing] =
    test(language.getClass.getSimpleName.replace("$", "")) {
      val text = "This is a test"
      val code = interpolation(BlockContentContext(StringContext(text)))(Seq.empty)
      assertTrue(code == Code(RichText.fromString(text), language))
    }

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("We should define code content easily")(
      codeTest(Language.Abap, _.abap),
      codeTest(Language.Arduino, _.arduino),
      codeTest(Language.Bash, _.bash),
      codeTest(Language.Basic, _.basic),
      codeTest(Language.C, _.c),
      codeTest(Language.Clojure, _.clojure),
      codeTest(Language.Coffeescript, _.coffeescript),
      codeTest(Language.Cplusplus, _.cplusplus),
      codeTest(Language.Csharp, _.csharp),
      codeTest(Language.Css, _.css),
      codeTest(Language.Dart, _.dart),
      codeTest(Language.Diff, _.diff),
      codeTest(Language.Docker, _.docker),
      codeTest(Language.Elixir, _.elixir),
      codeTest(Language.Elm, _.elm),
      codeTest(Language.Erlang, _.erlang),
      codeTest(Language.Flow, _.flow),
      codeTest(Language.Fortran, _.fortran),
      codeTest(Language.Fsharp, _.fsharp),
      codeTest(Language.Gherkin, _.gherkin),
      codeTest(Language.Glsl, _.glsl),
      codeTest(Language.Go, _.go),
      codeTest(Language.Graphql, _.graphql),
      codeTest(Language.Groovy, _.groovy),
      codeTest(Language.Haskell, _.haskell),
      codeTest(Language.Html, _.html),
      codeTest(Language.Java, _.java),
      codeTest(Language.Javascript, _.javascript),
      codeTest(Language.Json, _.json),
      codeTest(Language.Julia, _.julia),
      codeTest(Language.Kotlin, _.kotlin),
      codeTest(Language.Latex, _.latex),
      codeTest(Language.Less, _.less),
      codeTest(Language.Lisp, _.lisp),
      codeTest(Language.Livescript, _.livescript),
      codeTest(Language.Lua, _.lua),
      codeTest(Language.Makefile, _.makefile),
      codeTest(Language.Markdown, _.markdown),
      codeTest(Language.Markup, _.markup),
      codeTest(Language.Matlab, _.matlab),
      codeTest(Language.Mermaid, _.mermaid),
      codeTest(Language.Nix, _.nix),
      codeTest(Language.ObjectiveC, _.objectiveC),
      codeTest(Language.Ocaml, _.ocaml),
      codeTest(Language.Pascal, _.pascal),
      codeTest(Language.Perl, _.perl),
      codeTest(Language.Php, _.php),
      codeTest(Language.PlainText, _.plainText),
      codeTest(Language.Powershell, _.powershell),
      codeTest(Language.Prolog, _.prolog),
      codeTest(Language.Protobuf, _.protobuf),
      codeTest(Language.Python, _.python),
      codeTest(Language.R, _.r),
      codeTest(Language.Reason, _.reason),
      codeTest(Language.Ruby, _.ruby),
      codeTest(Language.Rust, _.rust),
      codeTest(Language.Sass, _.sass),
      codeTest(Language.Scala, _.scala),
      codeTest(Language.Scheme, _.scheme),
      codeTest(Language.Scss, _.scss),
      codeTest(Language.Shell, _.shell),
      codeTest(Language.Sql, _.sql),
      codeTest(Language.Swift, _.swift),
      codeTest(Language.Typescript, _.typescript),
      codeTest(Language.VbDotNet, _.vbDotNet),
      codeTest(Language.Verilog, _.verilog),
      codeTest(Language.Vhdl, _.vhdl),
      codeTest(Language.VisualBasic, _.visualBasic),
      codeTest(Language.Webassembly, _.webassembly),
      codeTest(Language.Xml, _.xml),
      codeTest(Language.Yaml, _.yaml),
      codeTest(Language.JavaOrCs, _.javaOrCs)
    )
}
