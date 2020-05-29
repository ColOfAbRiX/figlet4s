package dependencies

import sbt._

/**
 * Compiler plugins
 */
trait CompilerPlugins {

  // Versions
  lazy val BetterMonadicForVersion = "0.3.1"
  lazy val KindProjectorVersion    = "0.10.3"
  lazy val SplainVersion           = "0.5.5"
  lazy val WartRemoverVersion      = "2.4.7"

  // Dependencies
  lazy val BetterMonadicForPlugin = compilerPlugin("com.olegpy"    %% "better-monadic-for" % BetterMonadicForVersion)
  lazy val KindProjectorPlugin    = compilerPlugin("org.typelevel" %% "kind-projector"     % KindProjectorVersion)
  lazy val SplainPlugin           = compilerPlugin("io.tryp"       % "splain"              % SplainVersion cross CrossVersion.patch)
  lazy val WartremoverPlugin = compilerPlugin(
    "org.wartremover" %% "wartremover" % WartRemoverVersion cross CrossVersion.full,
  )

}
