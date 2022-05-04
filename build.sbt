lazy val scala213 = "2.13.8"

lazy val supportedScalaVersions = List(scala213)

// Common configuration
inThisBuild(
  List(
    scalaVersion         := scala213,
    crossScalaVersions   := supportedScalaVersions,
    version              := "0.1.0",
    description          := "A strongly typed interface to interact with Notion using ZIO",
    organization         := "io.univalence",
    organizationName     := "Univalence",
    organizationHomepage := Some(url("https://univalence.io/")),
    startYear            := Some(2022),
    developers           := List(),
    homepage             := Some(url("https://github.com/univalence/zio-notion")),
    licenses             := List("Apache-2.0" -> url("https://github.com/univalence/zio-notion/blob/master/LICENSE")),
    versionScheme        := Some("early-semver"),
    version ~= addVersionPadding
  )
)

// Scalafix configuration
ThisBuild / scalafixScalaBinaryVersion := "2.13"
ThisBuild / semanticdbEnabled          := true
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies ++= Seq("com.github.vovapolu" %% "scaluzzi" % "0.1.21")

// SCoverage configuration
val excludedPackages: Seq[String] = Seq.empty

ThisBuild / coverageFailOnMinimum           := false
ThisBuild / coverageMinimumStmtTotal        := 80
ThisBuild / coverageMinimumBranchTotal      := 80
ThisBuild / coverageMinimumStmtPerPackage   := 50
ThisBuild / coverageMinimumBranchPerPackage := 50
ThisBuild / coverageMinimumStmtPerFile      := 0
ThisBuild / coverageMinimumBranchPerFile    := 0
ThisBuild / coverageExcludedPackages        := excludedPackages.mkString(";")

// Aliases
addCommandAlias("fmt", "scalafmt")
addCommandAlias("fmtCheck", "scalafmtCheckAll")
addCommandAlias("lint", "scalafix")
addCommandAlias("lintCheck", "scalafixAll --check")
addCommandAlias("check", "; fmtCheck; lintCheck;")
addCommandAlias("fixStyle", "; scalafmtAll; scalafixAll;")
addCommandAlias("prepare", "fixStyle")
addCommandAlias("testAll", "; clean;+ test;")
addCommandAlias("testSpecific", "; clean; test;")
addCommandAlias("testSpecificWithCoverage", "; clean; coverage; test; coverageReport;")

// -- Lib versions
val zio       = "2.0.0-RC5"
val zioConfig = "3.0.0-RC8"
val zioHttp   = "2.0.0-RC7"
val circe     = "0.14.1"
val sttp      = "3.5.2"

// -- Main project settings
lazy val core =
  (project in file("zio-notion-core"))
    .settings(
      name := "zio-notion",
      scalacOptions ~= fatalWarningsAsProperties,
      libraryDependencies ++= Seq(
        "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % sttp,
        "com.softwaremill.sttp.client3" %% "core"                          % sttp,
        "com.softwaremill.sttp.client3" %% "httpclient-backend-zio"        % sttp,
        "dev.zio"                       %% "zio"                           % zio,
        "dev.zio"                       %% "zio-config"                    % zioConfig,
        "dev.zio"                       %% "zio-config"                    % zioConfig,
        "dev.zio"                       %% "zio-config-magnolia"           % zioConfig,
        "io.d11"                        %% "zhttp"                         % zioHttp,
        "io.circe"                      %% "circe-core"                    % circe,
        "io.circe"                      %% "circe-parser"                  % circe,
        "io.circe"                      %% "circe-generic"                 % circe,
        "io.circe"                      %% "circe-generic-extras"          % circe,
        "dev.zio"                       %% "zio-test"                      % zio     % Test,
        "dev.zio"                       %% "zio-test-sbt"                  % zio     % Test,
        "io.d11"                        %% "zhttp-test"                    % zioHttp % Test
      ),
      testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
      scalacOptions ++= Seq("-Ymacro-annotations")
    )

/**
 * Don't fail the compilation for warnings by default, you can still
 * activate it using system properties (It should always be activated in
 * the CI).
 */
def fatalWarningsAsProperties(options: Seq[String]): Seq[String] =
  if (sys.props.getOrElse("fatal-warnings", "false") == "true") options
  else options.filterNot(Set("-Xfatal-warnings"))

/**
 * Add padding to change: 0.1.0+48-bfcea99ap20220317-1157-SNAPSHOT into
 * 0.1.0+0048-bfcea99ap20220317-1157-SNAPSHOT. It helps to retrieve the
 * latest snapshots from
 * https://oss.sonatype.org/#nexus-search;gav~io.univalence~zio-spark_2.13~~~~kw,versionexpand.
 */
def addVersionPadding(baseVersion: String): String = {
  import scala.util.matching.Regex

  val paddingSize    = 5
  val counter: Regex = "\\+([0-9]+)-".r

  counter.findFirstMatchIn(baseVersion) match {
    case Some(regex) =>
      val count          = regex.group(1)
      val snapshotNumber = "0" * (paddingSize - count.length) + count
      counter.replaceFirstIn(baseVersion, s"+$snapshotNumber-")
    case None => baseVersion
  }
}
