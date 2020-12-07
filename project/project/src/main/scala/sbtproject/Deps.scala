package sbtproject

import sbt._

/**
 * SBT plugins dependencies
 */
object Dependencies {

  lazy val ErrorsSummaryVersion = "0.6.3"
  lazy val SbtStatsVersion      = "1.0.7"
  lazy val UnidocVersion        = "0.4.3"
  lazy val UpdatesVersion       = "0.5.1"

  lazy val ErrorsSummaryDep = "org.duhemm"       % "sbt-errors-summary" % ErrorsSummaryVersion
  lazy val SbtStatsDep      = "com.orrsella"     % "sbt-stats"          % SbtStatsVersion
  lazy val UnidocDep        = "com.eed3si9n"     % "sbt-unidoc"         % UnidocVersion
  lazy val UpdatesDep       = "com.timushev.sbt" % "sbt-updates"        % UpdatesVersion

  lazy val sbtPlugins = Seq(
    ErrorsSummaryDep,
    SbtStatsDep,
    UnidocDep,
    UpdatesDep,
  )

}