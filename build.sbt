import dependencies.Dependencies._

// General
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / organization := "com.colofabrix.scala.figlet4s"
ThisBuild / scalaVersion := ScalaLangVersion
ThisBuild / turbo := true
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "@ColOfAbRiX", url("http://github.com/ColOfAbRiX")),
)

// Compiler options
// ThisBuild / scalacOptions ++= Compiler.TpolecatOptions ++ Compiler.StrictOptions ++ Seq("-P:splain:all")
ThisBuild / scalacOptions ++= Compiler.TpolecatOptions ++ Seq("-P:splain:all")

// Wartremover
// ThisBuild / wartremoverExcluded ++= (baseDirectory.value * "**" / "src" / "test").get
// ThisBuild / wartremoverErrors ++= Warts.allBut(
//   Wart.Any,
//   Wart.ImplicitParameter,
//   Wart.Nothing,
//   Wart.Overloading,
//   Wart.ToString,
// )

// Scalafmt
ThisBuild / scalafmtOnCompile := true

// Global dependencies and compiler plugins
ThisBuild / libraryDependencies ++= Seq(
  SplainPlugin,
  WartremoverPlugin,
)

// Root project
lazy val figlet4s: Project = project
  .in(file("."))
  .settings(
    name := "figlet4s",
    description := "Scala FIGlet implementation",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      PPrintDep,
      EnumeratumDep,
    ),
    bundledDependencies ++= Seq(
      CatsBundle
    ),
  )
