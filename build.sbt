import dependencies.Dependencies._

val figlet4sVersion = "0.1.0"

// General
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / organization := "com.colofabrix.scala.figlet4s"
ThisBuild / scalaVersion := ScalaLangVersion
ThisBuild / turbo := true
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "@ColOfAbRiX", url("http://github.com/ColOfAbRiX")),
)

// Compiler options
ThisBuild / scalacOptions ++= Compiler.TpolecatOptions_2_13 ++ Compiler.StrictOptions ++ Seq("-P:splain:all")

// Wartremover
ThisBuild / wartremoverExcluded ++= (baseDirectory.value * "**" / "src" / "test").get
ThisBuild / wartremoverErrors ++= Warts.allBut(
  Wart.Any,
  Wart.DefaultArguments,
  Wart.Nothing,
  Wart.Overloading,
  Wart.StringPlusAny,
  Wart.ToString,
)

// Scalafmt
ThisBuild / scalafmtOnCompile := true

// Global dependencies and compiler plugins
ThisBuild / libraryDependencies ++= Seq(
  SplainPlugin,
  WartremoverPlugin,
)

// Figlet4s
lazy val figlet4s: Project = project.in(file("."))

// Figlet4s Core project
lazy val figlet4sCore: Project = project
  .in(file("figlet4s-core"))
  .settings(
    name := "figlet4s",
    description := "Scala FIGlet implementation",
    version := figlet4sVersion,
    libraryDependencies ++= Seq(
      CatsCoreDep,
      CatsEffectDep,
      CatsKernelDep,
      EnumeratumDep,
      ScalaTestFlatSpecDep,
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
