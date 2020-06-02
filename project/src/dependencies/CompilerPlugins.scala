package dependencies

import sbt._

/**
 * Compiler plugins
 */
trait CompilerPlugins {

  // Versions
  lazy val SplainVersion      = "0.5.5"
  lazy val WartRemoverVersion = "2.4.7"

  // Dependencies
  lazy val SplainPlugin = compilerPlugin("io.tryp" % "splain" % SplainVersion cross CrossVersion.patch)
  lazy val WartremoverPlugin = compilerPlugin(
    "org.wartremover" %% "wartremover" % WartRemoverVersion cross CrossVersion.full,
  )

}
