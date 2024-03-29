import sbt._

/**
 * SBT plugins dependencies for Code Quality
 */
object CodeQualityDependencies {

  lazy val ScalaFixVersion    = "0.9.27"
  lazy val ScalafmtVersion    = "2.4.3"
  lazy val SCoverageVersion   = "1.9.1"
  lazy val WartremoverVersion = "2.4.16"

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
