import sbt._
import sbtproject._
import sbtproject.utils._
import sbtproject.Dependencies._
import xerial.sbt.Sonatype._

// General
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / organization := "com.colofabrix.scala"
ThisBuild / licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / scalaVersion := ScalaLangVersion
ThisBuild / turbo := true

val commonSettings: Seq[Def.Setting[_]] = Seq(
  Test / logBuffered := false,

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
  Compile / scalafmtOnCompile := true,
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
    Wart.PublicInference
  ),

  // Scaladoc
  Compile / autoAPIMappings := true,
  Compile / doc / scalacOptions := Seq(
    "-doc-title", "Figlet4s API Documentation",
    "-doc-version", version.value,
    "-encoding", "UTF-8"
  )
)

// GIT version information
ThisBuild / dynverVTagPrefix := false
ThisBuild / dynverSeparator := "-"

// Scalafix
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.4.3"
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

// Code coverage
ThisBuild / coverageMinimum := 75
ThisBuild / coverageFailOnMinimum := true

// Publishing
ThisBuild / sonatypeProjectHosting := Some(
  GitHubHosting("ColOfAbRiX", "figlet4s", "colofabrix@tin.it")
)
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "colofabrix@tin.it", url("http://github.com/ColOfAbRiX")),
)
ThisBuild / publishTo := sonatypePublishTo.value
ThisBuild / publishMavenStyle := true

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
