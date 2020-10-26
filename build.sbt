import sbt._
import sbtproject._
import sbtproject.Dependencies._
import sbtproject.Configurations._

// General
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / organization := "com.colofabrix.scala.figlet4s"
ThisBuild / scalaVersion := ScalaLangVersion
ThisBuild / turbo := true
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "@ColOfAbRiX", url("http://github.com/ColOfAbRiX")),
)

val commonSettings: Seq[Def.Setting[_]] = Seq(
  Test / logBuffered := false,

  // Compiler options
  scalacOptions := Compiler.TpolecatOptions_2_13 ++ Compiler.StrictOptions,
  Test / scalacOptions := Compiler.TpolecatOptions_2_13,

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
  Compile / wartremoverWarnings := Seq.empty,

  // Scaladoc
  Compile / autoAPIMappings := true,
  Compile / doc / scalacOptions := Seq(
    "-doc-title", "Figlet4s API Documentation",
    "-doc-version", version.value,
    "-encoding", "UTF-8"
  )
)

// Scalafmt
ThisBuild / Compile / scalafmtOnCompile := true

// Scalafix
ThisBuild / Compile / scalafixOnCompile := true
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.4.0"
ThisBuild / scalafixScalaBinaryVersion := ScalaLangVersion.replaceAll("\\.\\d+$", "")
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

// GIT version information
ThisBuild / dynverVTagPrefix := false
ThisBuild / dynverSeparator := "-"

// Figlet4s
lazy val figlet4s: Project = project
  .in(file("."))
  .aggregate(figlet4sCore, figlet4sEffects)
  .enablePlugins(ScalaUnidocPlugin)
  .settings(
    name := "figlet4s",
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
  .configs(Benchmark)
  .dependsOn(figlet4sCore)
  .settings(commonSettings)
  .settings(
    name := "figlet4s-benchmarks",
    description := "Benchmarks for Figlet4s",
    inConfig(Benchmark)(Defaults.testSettings),
    Benchmark / parallelExecution := false,
    publishArtifact := false,
    resolvers ++= SonatypeRepos,
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    libraryDependencies ++= Seq(
      CatsCoreDep,
      ScalameterDep,
    ),
  )
