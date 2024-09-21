import sbt._

/**
 * SBT plugins dependencies for Code Quality
 */
object CodeQualityDeps {

  lazy val ScalaFixVersion    = "0.12.1"
  lazy val ScalafmtVersion    = "2.4.6"
  lazy val SCoverageVersion   = "2.2.0"
  lazy val WartremoverVersion = "3.2.1"

  lazy val ScalaFixDep    = "ch.epfl.scala"   % "sbt-scalafix"    % ScalaFixVersion
  lazy val ScalafmtDep    = "org.scalameta"   % "sbt-scalafmt"    % ScalafmtVersion
  lazy val SCoverageDep   = "org.scoverage"   % "sbt-scoverage"   % SCoverageVersion
  lazy val WartremoverDep = "org.wartremover" % "sbt-wartremover" % WartremoverVersion

  lazy val codeQualitySbtPlugins = Seq(
    ScalaFixDep,
    ScalafmtDep,
    SCoverageDep,
    WartremoverDep,
  )

}
