import sbt._
import sbtproject._
import sbtproject.utils._
import sbtproject.Dependencies._
import xerial.sbt.Sonatype._

// General
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / turbo := true
ThisBuild / scalaVersion := ScalaLangVersion

// Project information
ThisBuild / homepage := Some(url("https://github.com/ColOfAbRiX/figlet4s"))
ThisBuild / organization := "com.colofabrix.scala"
ThisBuild / organizationName := "ColOfAbRiX"
ThisBuild / organizationHomepage := Some(url("https://github.com/ColOfAbRiX"))
ThisBuild / licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/ColOfAbRiX/figlet4s"), "scm:git@github.com:ColOfAbRiX/figlet4s.git"),
)
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "colofabrix@tin.it", url("http://github.com/ColOfAbRiX")),
)

// Publishing
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true
ThisBuild / sonatypeProjectHosting := Some(
  GitHubHosting("ColOfAbRiX", "figlet4s", "colofabrix@tin.it"),
)
ThisBuild / publishTo := Some(
  if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
  else Opts.resolver.sonatypeStaging,
)

// GIT version information
ThisBuild / dynverSonatypeSnapshots := true

// Scalafix
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.4.3"
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

val commonSettings: Seq[Def.Setting[_]] = Seq(
  // Testing
  Test / logBuffered := false,
  Test / testOptions += Tests.Argument("-oFD"),

  // Compiler options
  scalacOptions := versioned(scalaVersion.value)(
    Compiler.Options_2_12 ++ Compiler.StrictOptions,
    Compiler.Options_2_13 ++ Compiler.StrictOptions,
  ),
  Test / scalacOptions := versioned(scalaVersion.value)(
    Compiler.Options_2_12,
    Compiler.Options_2_13,
  ),

  // Cross Scala Versions
  crossScalaVersions := SupportedScalaLangVersion,

  // Code styling
  // Compile / scalafmtOnCompile := true,
  // Compile / scalafixOnCompile := true,

  // Wartremover
  Compile / wartremoverErrors := Warts.allBut(
    Wart.Any,
    Wart.DefaultArguments,
    Wart.Nothing,
    Wart.Overloading,
    Wart.StringPlusAny,
    Wart.ToString,
    // Covered by ScalaFix
    Wart.PublicInference,
  ),

  // Scaladoc
  Compile / autoAPIMappings := true,
  Compile / doc / scalacOptions ++= Seq(
    "-doc-title", "Figlet4s API Documentation",
    "-doc-version", version.value,
    "-encoding", "UTF-8",
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
  .aggregate(figlet4sCore, figlet4sEffects)
  .enablePlugins(ScalaUnidocPlugin)
  .settings(
    name := "figlet4s",
    crossScalaVersions := Nil,
    publish / skip := true,
  )

// Figlet4s Core project
lazy val figlet4sCore: Project = project
  .in(file("figlet4s-core"))
  .settings(commonSettings)
  .settings(
    name := "figlet4s-core",
    description := "Scala FIGlet implementation",
    libraryDependencies ++= Seq(
      CatsCoreDep,
      CatsEffectDep,
      CatsKernelDep,
      EnumeratumDep,
      ScalaTestFlatSpecDep,
      ScalaTestPlusCheckDep,
      ScalaTestShouldMatchersDep,
    ),
  )

// Figlet4s Effects project
lazy val figlet4sEffects: Project = project
  .in(file("figlet4s-effects"))
  .dependsOn(figlet4sCore)
  .settings(commonSettings)
  .settings(
    name := "figlet4s-effects",
    description := "Effects extension for Figlet4s",
    libraryDependencies ++= Seq(
      CatsCoreDep,
      CatsEffectDep,
      CatsKernelDep,
      ScalaTestFlatSpecDep,
      ScalaTestShouldMatchersDep,
    ),
  )

// Figlet4s Benchmarks project
lazy val figlet4sBenchmarks: Project = project
  .in(file("figlet4s-benchmarks"))
  .dependsOn(figlet4sCore)
  .settings(
    name := "figlet4s-benchmarks",
    description := "Benchmarks for Figlet4s",
    publishArtifact := false,
    logBuffered := false,
    Test / parallelExecution := false,
    resolvers ++= SonatypeRepos,
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    libraryDependencies ++= Seq(
      CatsCoreDep,
      ScalameterDep,
    ),
  )
