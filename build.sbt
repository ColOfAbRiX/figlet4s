import sbtproject._
import sbtproject.dependencies.AllDependencies._
import sbtproject.settings.Configurations._

val figlet4sVersion = "0.1.0"

// General
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / organization := "com.colofabrix.scala.figlet4s"
ThisBuild / scalaVersion := ScalaLangVersion
ThisBuild / turbo := true
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "@ColOfAbRiX", url("http://github.com/ColOfAbRiX")),
)

// Global dependencies and compiler plugins
ThisBuild / libraryDependencies ++= Seq(SplainPlugin)

// Compile options
figlet4s / Compile / scalacOptions := Compiler.TpolecatOptions_2_13 ++ Compiler.StrictOptions ++ Compiler.SplainOptions
figlet4sCore / Compile / scalacOptions := (figlet4s / Compile / scalacOptions).value
figlet4sEffects / Compile / scalacOptions := (figlet4s / Compile / scalacOptions).value

// Test options
ThisBuild / Test / logBuffered := false
figlet4s / Test / scalacOptions := Compiler.TpolecatOptions_2_13
figlet4sCore / Test / scalacOptions := (figlet4s / Test / scalacOptions).value
figlet4sEffects / Test / scalacOptions := (figlet4s / Test / scalacOptions).value

// Scaladoc
Compile / doc / scalacOptions := Seq(
  "-doc-title", "Figlet4s API Documentation",
  "-doc-version", figlet4sVersion,
  "-groups", "-author", "-implicits",
  "-encoding", "UTF-8"
)

// Wartremover
figlet4s / Compile / wartremoverErrors := Warts.allBut(
  Wart.Any,
  Wart.DefaultArguments,
  Wart.Nothing,
  Wart.Overloading,
  Wart.StringPlusAny,
  Wart.ToString,
  // Covered by ScalaFix
  Wart.PublicInference
)
figlet4sCore / Compile / wartremoverErrors := (figlet4s / Compile / wartremoverErrors).value
figlet4sEffects / Compile / wartremoverErrors := (figlet4s / Compile / wartremoverErrors).value
figlet4s / Test / wartremoverErrors := Seq.empty
figlet4sCore / Test / wartremoverErrors := (figlet4s / Test / wartremoverErrors).value
figlet4sEffects / Test / wartremoverErrors := (figlet4s / Test / wartremoverErrors).value
figlet4s / Test / wartremoverWarnings := (figlet4s / Compile / wartremoverErrors).value
figlet4sCore / Test / wartremoverWarnings := (figlet4s / Test / wartremoverWarnings).value
figlet4sEffects / Test / wartremoverWarnings := (figlet4s / Test / wartremoverWarnings).value

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
  .settings(
    name := "figlet4s",
  )

// Figlet4s Core project
lazy val figlet4sCore: Project = project
  .in(file("figlet4s-core"))
  .settings(
    name := "figlet4s-core",
    description := "Scala FIGlet implementation",
    version := figlet4sVersion,
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
  .settings(
    name := "figlet4s-effects",
    description := "Effects extension for Figlet4s",
    version := figlet4sVersion,
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
  .settings(
    name := "figlet4s-benchmarks",
    description := "Benchmarks for Figlet4s",
    inConfig(Benchmark)(Defaults.testSettings),
    Benchmark / parallelExecution := false,
    publishArtifact := false,
    resolvers ++= SonatypeRepos,
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    version := figlet4sVersion,
    libraryDependencies ++= Seq(
      CatsCoreDep,
      ScalameterDep,
    ),
  )
