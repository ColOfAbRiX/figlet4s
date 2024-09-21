import sbt.*

/**
 * SBT plugins dependencies
 */
object PluginDeps {

  lazy val ErrorsSummaryVersion = "0.6.5"
  lazy val MicrositesVersion    = "1.3.4"
  lazy val SbtStatsVersion      = "1.0.7"
  lazy val SbtTPoleCatVersion   = "0.5.2"
  lazy val UnidocVersion        = "0.5.0"
  lazy val UpdatesVersion       = "0.6.4"

  lazy val ErrorsSummaryDep = "com.github.duhemm" % "sbt-errors-summary" % ErrorsSummaryVersion
  lazy val MicrositesDep    = "com.47deg"         % "sbt-microsites"     % MicrositesVersion
  lazy val SbtStatsDep      = "com.orrsella"      % "sbt-stats"          % SbtStatsVersion
  lazy val SbtTPoleCatDep   = "org.typelevel"     % "sbt-tpolecat"       % SbtTPoleCatVersion
  lazy val UnidocDep        = "com.github.sbt"    % "sbt-unidoc"         % UnidocVersion
  lazy val UpdatesDep       = "com.timushev.sbt"  % "sbt-updates"        % UpdatesVersion

  lazy val sbtPlugins = Seq(
    ErrorsSummaryDep,
//    MicrositesDep,
    SbtStatsDep,
    SbtTPoleCatDep,
    UnidocDep,
    UpdatesDep,
  )

}
