import sbt._
import Dependencies._
import TestDependencies._
import utils._
import xerial.sbt.Sonatype._
import org.typelevel.scalacoptions.ScalacOptions

// General
Global / onChangedBuildSource := ReloadOnSourceChanges
Global / lintUnusedKeysOnLoad := false
ThisBuild / turbo             := true
ThisBuild / scalaVersion      := ScalaLangVersion

// Project information
ThisBuild / name                 := "figlet4s"
ThisBuild / startYear            := Some(2020)
ThisBuild / homepage             := Some(url("https://github.com/ColOfAbRiX/figlet4s"))
ThisBuild / organization         := "com.colofabrix.scala"
ThisBuild / organizationName     := "ColOfAbRiX"
ThisBuild / organizationHomepage := Some(url("https://github.com/ColOfAbRiX"))
ThisBuild / licenses             := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/ColOfAbRiX/figlet4s"), "scm:git@github.com:ColOfAbRiX/figlet4s.git"),
)
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "colofabrix@tin.it", url("http://github.com/ColOfAbRiX")),
)

// Publishing - using Sonatype Central (new API)
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle    := true
ThisBuild / sonatypeProjectHosting := Some(
  GitHubHosting("ColOfAbRiX", "figlet4s", "colofabrix@tin.it"),
)
ThisBuild / publishTo := sonatypePublishToBundle.value

// GIT version information
ThisBuild / dynverSonatypeSnapshots := true

// Scalafix
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

addCommandAlias("styleApply", "; scalafmtAll; Compile / scalafixAll")
addCommandAlias("styleCheck", "; scalafmtCheckAll; Compile / scalafixAll --check")

val commonScalaSettings: Seq[Def.Setting[_]] = Seq(
  // Testing
  Test / testOptions += Tests.Argument("-oFD"),
  // Cross Scala Versions
  crossScalaVersions := SupportedScalaLangVersion,
  // sbt-tpolecat: Use CI mode for strict checks, relax for tests
  Test / tpolecatExcludeOptions ++= ScalacOptions.warnUnusedOptions,
  // Relax Scala 3 syntax warnings for test code (tests use Scala 2 style)
  Test / scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, _)) => Seq(
      "-source:3.0-migration",  // Enable migration mode for Scala 2 -> 3 syntax
      "-Wconf:any:s",           // Silence all warnings for tests
    )
    case _ => Nil
  }),
  // Disable fatal warnings and wartremover for tests (they use Scala 2 style syntax)
  Test / scalacOptions ~= { opts =>
    opts.filterNot(o =>
      o == "-Werror" ||
      o == "-Xfatal-warnings" ||
      o.startsWith("-P:wartremover")
    )
  },
  // Required for Enumeratum's findValues macro in Scala 3
  scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, _)) => Seq("-Yretain-trees")
    case _            => Nil
  }),
  // Wartremover - only enabled for Compile scope
  Compile / wartremoverErrors := Warts.allBut(
    Wart.Any,
    Wart.DefaultArguments,
    Wart.Nothing,
    Wart.Overloading,
    Wart.StringPlusAny,
    Wart.ToString,
    Wart.TripleQuestionMark,
    // Covered by ScalaFix
    Wart.PublicInference,
  ),
  // Disable Wartremover for tests
  Test / wartremoverErrors := Nil,
  Test / wartremoverWarnings := Nil,
  // Scaladoc
  Compile / autoAPIMappings := true,
  Compile / doc / scalacOptions ++= versioned(scalaVersion.value)(
    Seq(
      "-doc-title", "Figlet4s API Documentation",
      "-doc-version", version.value,
      "-encoding", "UTF-8",
    ),
    Seq(
      "-project", "Figlet4s API Documentation",
      "-project-version", version.value,
    ),
  ),
  // Packaging and publishing
  Compile / packageBin / packageOptions ++= Seq(
    Package.ManifestAttributes(
      ("Git-Build-Branch", git.gitCurrentBranch.value),
      ("Git-Head-Commit-Date", git.gitHeadCommitDate.value.getOrElse("")),
      ("Git-Head-Commit", git.gitHeadCommit.value.getOrElse("")),
      ("Git-Uncommitted-Changes", git.gitUncommittedChanges.value.toString),
    ),
  ),
)

// Figlet4s
lazy val figlet4s: Project = project
  .in(file("."))
  .aggregate(figlet4sCore, figlet4sEffects, figlet4sJava)
  .enablePlugins(ScalaUnidocPlugin)
  .settings(
    name               := "figlet4s",
    crossScalaVersions := Nil,
    publish / skip     := true,
    onLoadMessage :=
      """
        |    _____ _       _      _   _  _
        |   |  ___(_) __ _| | ___| |_| || |  ___
        |   | |_  | |/ _` | |/ _ \ __| || |_/ __|
        |   |  _| | | (_| | |  __/ |_|__   _\__ \
        |   |_|   |_|\__, |_|\___|\__|  |_| |___/
        |            |___/
        |     Welcome to the build for Figlet4s
        |
        |""".stripMargin,
    ScalaUnidoc / unidoc / unidocProjectFilter := inAnyProject -- inProjects(figlet4sJava),
  )

// Figlet4s Core project
lazy val figlet4sCore: Project = project
  .in(file("figlet4s-core"))
  .settings(commonScalaSettings)
  .settings(
    name        := "figlet4s-core",
    description := "ASCII-art banners in Scala",
    libraryDependencies ++= Seq(
      CatsCoreDep,
      CatsEffectDep,
      CatsKernelDep,
      catsScalaTestDep(scalaVersion.value),
      EnumeratumDep,
      ScalaMockDep,
      ScalaTestFlatSpecDep,
      ScalaTestPlusCheckDep,
      ScalaTestShouldMatchersDep,
    ),
  )

// Figlet4s Effects project
lazy val figlet4sEffects: Project = project
  .in(file("figlet4s-effects"))
  .dependsOn(figlet4sCore % "compile->compile;test->test")
  .settings(commonScalaSettings)
  .settings(
    name        := "figlet4s-effects",
    description := "Effects extension for Figlet4s",
    libraryDependencies ++= Seq(
      CatsCoreDep,
      CatsEffectDep,
      CatsKernelDep % Runtime,
      catsScalaTestDep(scalaVersion.value),
      ScalaTestFlatSpecDep,
      ScalaTestShouldMatchersDep,
    ),
  )

// Figlet4s Java integration project
lazy val figlet4sJava: Project = project
  .in(file("figlet4s-java"))
  .dependsOn(figlet4sCore % "compile->compile;test->test")
  .settings(
    name                         := "figlet4s-java",
    description                  := "Java integration for Figlet4s",
    javacOptions                ++= Compiler.JavacOptions,
    Compile / doc / javacOptions := Seq(),
    crossPaths                   := false,
    libraryDependencies ++= Seq(
      ScalaTestFlatSpecDep,
      ScalaTestShouldMatchersDep,
    ),
  )

lazy val figlet4sMicrosite = project
  .in(file("figlet4s-microsite"))
  .enablePlugins(MicrositesPlugin)
  .settings(
    crossScalaVersions           := SupportedScalaLangVersion,
    micrositeAnalyticsToken      := "UA-189728436-1",
    micrositeAuthor              := "ColOfAbRiX",
    micrositeBaseUrl             := "figlet4s",
    micrositeDescription         := "ASCII-art banners in Scala",
    micrositeDocumentationUrl    := "docs/",
    micrositeGithubOwner         := "ColOfAbRiX",
    micrositeGithubRepo          := "figlet4s",
    micrositeGithubToken         := sys.env.get("GITHUB_TOKEN"),
    micrositeGitterChannel       := false,
    micrositeHighlightLanguages ++= Seq("xml", "plaintext"),
    micrositeHighlightTheme      := "atom-one-dark", // https://highlightjs.org/static/demo/
    micrositeHomepage            := "https://colofabrix.github.io/",
    micrositeName                := "Figlet4s",
    micrositePushSiteWith        := GitHub4s,
    name                         := "figlet4s-microsite",
    mdocExtraArguments           := Seq("--no-link-hygiene"),
    publish / skip               := true,
    mdocVariables := Map(
      "VERSION"       -> """\d+\.\d+\.\d""".r.findFirstIn(version.value).getOrElse(""),
      "SCALA_VERSION" -> """\d+\.\d+""".r.findFirstIn(scalaVersion.value).getOrElse(""),
    ),
  )
