import sbt._

/**
 * SBT plugins dependencies
 */
object Plugins {

  lazy val ErrorsSummaryVersion = "0.6.3"
  lazy val MicrositesVersion    = "1.3.1"
  lazy val SbtStatsVersion      = "1.0.7"
  lazy val UnidocVersion        = "0.4.3"
  lazy val UpdatesVersion       = "0.5.1"

  lazy val ErrorsSummaryDep = "org.duhemm"       % "sbt-errors-summary" % ErrorsSummaryVersion
  lazy val MicrositesDep    = "com.47deg"        % "sbt-microsites"     % MicrositesVersion
  lazy val SbtStatsDep      = "com.orrsella"     % "sbt-stats"          % SbtStatsVersion
  lazy val UnidocDep        = "com.eed3si9n"     % "sbt-unidoc"         % UnidocVersion
  lazy val UpdatesDep       = "com.timushev.sbt" % "sbt-updates"        % UpdatesVersion

  lazy val sbtPlugins = Seq(
    ErrorsSummaryDep,
    SbtStatsDep,
    MicrositesDep,
    UnidocDep,
    UpdatesDep,
  )

}
