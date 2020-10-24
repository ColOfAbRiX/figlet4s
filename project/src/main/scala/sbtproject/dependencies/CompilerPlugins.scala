package sbtproject.dependencies

import sbt._

/**
 * Compiler plugins
 */
trait CompilerPlugins {

  // Versions
  lazy val SplainVersion = "0.5.7"

  // Dependencies
  lazy val SplainPlugin = compilerPlugin("io.tryp" % "splain" % SplainVersion cross CrossVersion.patch)

}
