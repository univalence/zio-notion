package zio.notion.model.common.enumeration

import io.circe._
import io.circe.Decoder.Result

sealed trait Language

object Language {
  case object Abap         extends Language
  case object Arduino      extends Language
  case object Bash         extends Language
  case object Basic        extends Language
  case object C            extends Language
  case object Clojure      extends Language
  case object Coffeescript extends Language
  case object Cplusplus    extends Language
  case object Csharp       extends Language
  case object Css          extends Language
  case object Dart         extends Language
  case object Diff         extends Language
  case object Docker       extends Language
  case object Elixir       extends Language
  case object Elm          extends Language
  case object Erlang       extends Language
  case object Flow         extends Language
  case object Fortran      extends Language
  case object Fsharp       extends Language
  case object Gherkin      extends Language
  case object Glsl         extends Language
  case object Go           extends Language
  case object Graphql      extends Language
  case object Groovy       extends Language
  case object Haskell      extends Language
  case object Html         extends Language
  case object Java         extends Language
  case object Javascript   extends Language
  case object Json         extends Language
  case object Julia        extends Language
  case object Kotlin       extends Language
  case object Latex        extends Language
  case object Less         extends Language
  case object Lisp         extends Language
  case object Livescript   extends Language
  case object Lua          extends Language
  case object Makefile     extends Language
  case object Markdown     extends Language
  case object Markup       extends Language
  case object Matlab       extends Language
  case object Mermaid      extends Language
  case object Nix          extends Language
  case object ObjectiveC   extends Language
  case object Ocaml        extends Language
  case object Pascal       extends Language
  case object Perl         extends Language
  case object Php          extends Language
  case object PlainText    extends Language
  case object Powershell   extends Language
  case object Prolog       extends Language
  case object Protobuf     extends Language
  case object Python       extends Language
  case object R            extends Language
  case object Reason       extends Language
  case object Ruby         extends Language
  case object Rust         extends Language
  case object Sass         extends Language
  case object Scala        extends Language
  case object Scheme       extends Language
  case object Scss         extends Language
  case object Shell        extends Language
  case object Sql          extends Language
  case object Swift        extends Language
  case object Typescript   extends Language
  case object VbDotNet     extends Language
  case object Verilog      extends Language
  case object Vhdl         extends Language
  case object VisualBasic  extends Language
  case object Webassembly  extends Language
  case object Xml          extends Language
  case object Yaml         extends Language
  case object JavaOrCs     extends Language

  implicit val codec: Codec[Language] =
    new Codec[Language] {

      override def apply(c: HCursor): Result[Language] =
        Decoder[String]
          .emap {
            case "abap"          => Right(Abap)
            case "arduino"       => Right(Arduino)
            case "bash"          => Right(Bash)
            case "basic"         => Right(Basic)
            case "c"             => Right(C)
            case "clojure"       => Right(Clojure)
            case "coffeescript"  => Right(Coffeescript)
            case "c++"           => Right(Cplusplus)
            case "c#"            => Right(Csharp)
            case "css"           => Right(Css)
            case "dart"          => Right(Dart)
            case "diff"          => Right(Diff)
            case "docker"        => Right(Docker)
            case "elixir"        => Right(Elixir)
            case "elm"           => Right(Elm)
            case "erlang"        => Right(Erlang)
            case "flow"          => Right(Flow)
            case "fortran"       => Right(Fortran)
            case "f#"            => Right(Fsharp)
            case "gherkin"       => Right(Gherkin)
            case "glsl"          => Right(Glsl)
            case "go"            => Right(Go)
            case "graphql"       => Right(Graphql)
            case "groovy"        => Right(Groovy)
            case "haskell"       => Right(Haskell)
            case "html"          => Right(Html)
            case "java"          => Right(Java)
            case "javascript"    => Right(Javascript)
            case "json"          => Right(Json)
            case "julia"         => Right(Julia)
            case "kotlin"        => Right(Kotlin)
            case "latex"         => Right(Latex)
            case "less"          => Right(Less)
            case "lisp"          => Right(Lisp)
            case "livescript"    => Right(Livescript)
            case "lua"           => Right(Lua)
            case "makefile"      => Right(Makefile)
            case "markdown"      => Right(Markdown)
            case "markup"        => Right(Markup)
            case "matlab"        => Right(Matlab)
            case "mermaid"       => Right(Mermaid)
            case "nix"           => Right(Nix)
            case "objective-c"   => Right(ObjectiveC)
            case "ocaml"         => Right(Ocaml)
            case "pascal"        => Right(Pascal)
            case "perl"          => Right(Perl)
            case "php"           => Right(Php)
            case "plain_text"    => Right(PlainText)
            case "powershell"    => Right(Powershell)
            case "prolog"        => Right(Prolog)
            case "protobuf"      => Right(Protobuf)
            case "python"        => Right(Python)
            case "r"             => Right(R)
            case "reason"        => Right(Reason)
            case "ruby"          => Right(Ruby)
            case "rust"          => Right(Rust)
            case "sass"          => Right(Sass)
            case "scala"         => Right(Scala)
            case "scheme"        => Right(Scheme)
            case "scss"          => Right(Scss)
            case "shell"         => Right(Shell)
            case "sql"           => Right(Sql)
            case "swift"         => Right(Swift)
            case "typescript"    => Right(Typescript)
            case "vb.net"        => Right(VbDotNet)
            case "verilog"       => Right(Verilog)
            case "vhdl"          => Right(Vhdl)
            case "visual_basic"  => Right(VisualBasic)
            case "webassembly"   => Right(Webassembly)
            case "xml"           => Right(Xml)
            case "yaml"          => Right(Yaml)
            case "java/c/c++/c#" => Right(JavaOrCs)
            case v               => Left(s"$v is not a valid language")
          }
          .apply(c)

      override def apply(language: Language): Json =
        Encoder[String]
          .contramap[Language] {
            case Abap         => "abap"
            case Arduino      => "arduino"
            case Bash         => "bash"
            case Basic        => "basic"
            case C            => "c"
            case Clojure      => "clojure"
            case Coffeescript => "coffeescript"
            case Cplusplus    => "c++"
            case Csharp       => "c#"
            case Css          => "css"
            case Dart         => "dart"
            case Diff         => "diff"
            case Docker       => "docker"
            case Elixir       => "elixir"
            case Elm          => "elm"
            case Erlang       => "erlang"
            case Flow         => "flow"
            case Fortran      => "fortran"
            case Fsharp       => "f#"
            case Gherkin      => "gherkin"
            case Glsl         => "glsl"
            case Go           => "go"
            case Graphql      => "graphql"
            case Groovy       => "groovy"
            case Haskell      => "haskell"
            case Html         => "html"
            case Java         => "java"
            case Javascript   => "javascript"
            case Json         => "json"
            case Julia        => "julia"
            case Kotlin       => "kotlin"
            case Latex        => "latex"
            case Less         => "less"
            case Lisp         => "lisp"
            case Livescript   => "livescript"
            case Lua          => "lua"
            case Makefile     => "makefile"
            case Markdown     => "markdown"
            case Markup       => "markup"
            case Matlab       => "matlab"
            case Mermaid      => "mermaid"
            case Nix          => "nix"
            case ObjectiveC   => "objective-c"
            case Ocaml        => "ocaml"
            case Pascal       => "pascal"
            case Perl         => "perl"
            case Php          => "php"
            case PlainText    => "plain text"
            case Powershell   => "powershell"
            case Prolog       => "prolog"
            case Protobuf     => "protobuf"
            case Python       => "python"
            case R            => "r"
            case Reason       => "reason"
            case Ruby         => "ruby"
            case Rust         => "rust"
            case Sass         => "sass"
            case Scala        => "scala"
            case Scheme       => "scheme"
            case Scss         => "scss"
            case Shell        => "shell"
            case Sql          => "sql"
            case Swift        => "swift"
            case Typescript   => "typescript"
            case VbDotNet     => "vb.net"
            case Verilog      => "verilog"
            case Vhdl         => "vhdl"
            case VisualBasic  => "visual basic"
            case Webassembly  => "webassembly"
            case Xml          => "xml"
            case Yaml         => "yaml"
            case JavaOrCs     => "java/c/c++/c#"
          }
          .apply(language)
    }
}
