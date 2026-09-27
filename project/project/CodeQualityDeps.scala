import sbt._

/**
 * SBT plugins dependencies for Code Quality
 */
object CodeQualityDependencies {

  lazy val ScalaFixVersion    = "0.14.9"
  lazy val ScalafmtVersion    = "2.6.2"
  lazy val SCoverageVersion   = "2.4.4"
  lazy val WartremoverVersion = "3.6.2"

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
