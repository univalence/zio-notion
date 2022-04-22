lazy val scala213 = "2.13.8"

lazy val supportedScalaVersions = List(scala213)

// Common configuration
inThisBuild(
  List(
    scalaVersion := scala213,
    crossScalaVersions := supportedScalaVersions,
    version := "0.1.0",
    description := "My fantastic project",
    organization := "io.univalence",
    organizationName := "Univalence",
    organizationHomepage := Some(url("https://univalence.io/")),
    startYear := Some(2022),
    developers := List(),
    homepage := Some(url("https://github.com/univalence/zio-spark")),
    licenses := List("Apache-2.0" -> url("https://github.com/univalence/zio-spark/blob/master/LICENSE")),
    versionScheme := Some("early-semver"),
    version ~= addVersionPadding
  )
)

// Scalafix configuration
ThisBuild / scalafixScalaBinaryVersion := "2.13"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies ++= Seq("com.github.vovapolu" %% "scaluzzi" % "0.1.21")

// SCoverage configuration
val excludedPackages: Seq[String] = Seq()

ThisBuild / coverageFailOnMinimum := false
ThisBuild / coverageMinimumStmtTotal := 80
ThisBuild / coverageMinimumBranchTotal := 80
ThisBuild / coverageMinimumStmtPerPackage := 50
ThisBuild / coverageMinimumBranchPerPackage := 50
ThisBuild / coverageMinimumStmtPerFile := 0
ThisBuild / coverageMinimumBranchPerFile := 0
ThisBuild / coverageExcludedPackages := excludedPackages.mkString(";")

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
lazy val libVersion = new {
  // -- Test
  val zio = "2.0.0-RC5"
  val zioConfig = "3.0.0-RC8"
  val zioPrelude = "1.0.0-RC13"
  val sttp = "3.5.2"
  val zioHttp = "2.0.0-RC7"
  val zioJson = "0.3.0-RC7"
  val circe = "0.14.1"
  val json4s = "4.0.5"
}

// -- Main project settings
lazy val app =
  (project in file("."))
    .settings(
      name := "notion-companion",
      scalacOptions ~= fatalWarningsAsProperties,
      libraryDependencies ++= Seq(
        "io.d11" %% "zhttp" % libVersion.zioHttp,
        "io.d11" %% "zhttp-test" % libVersion.zioHttp % Test,
        "dev.zio" %% "zio-test" % libVersion.zio % Test,
        "dev.zio" %% "zio-test-sbt" % libVersion.zio % Test,
        "dev.zio" %% "zio" % libVersion.zio,
        "dev.zio" %% "zio-prelude" % libVersion.zioPrelude,
        "dev.zio" %% "zio-config" % libVersion.zioConfig,
        "dev.zio" %% "zio-config-magnolia" % libVersion.zioConfig,
        "dev.zio" %% "zio-json" % libVersion.zioJson,
        "com.softwaremill.sttp.client3" %% "core" % libVersion.sttp,
        "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % libVersion.sttp,
        "com.softwaremill.sttp.client3" %% "httpclient-backend-zio" % libVersion.sttp,
        "io.circe" %% "circe-core" % libVersion.circe,
        "io.circe" %% "circe-generic" % libVersion.circe,
        "io.circe" %% "circe-parser" % libVersion.circe,
        "dev.zio" %% "zio-config" % libVersion.zioConfig,
        "org.json4s" %% "json4s-native" % libVersion.json4s
      )
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

  val paddingSize = 5
  val counter: Regex = "\\+([0-9]+)-".r

  counter.findFirstMatchIn(baseVersion) match {
    case Some(regex) =>
      val count = regex.group(1)
      val snapshotNumber = "0" * (paddingSize - count.length) + count
      counter.replaceFirstIn(baseVersion, s"+$snapshotNumber-")
    case None => baseVersion
  }
}