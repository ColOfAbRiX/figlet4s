import sbt._

/**
 * SBT plugins dependencies
 */
object SbtPluginsDeps {

  lazy val ErrorsSummaryVersion = "0.6.5"
  lazy val MicrositesVersion    = "1.4.4"
  lazy val TpolecatVersion      = "0.5.2"
  lazy val UnidocVersion        = "0.5.0"
  lazy val UpdatesVersion       = "0.6.4"

  lazy val ErrorsSummaryDep = "com.github.duhemm" % "sbt-errors-summary" % ErrorsSummaryVersion
  lazy val MicrositesDep    = "com.47deg"         % "sbt-microsites"     % MicrositesVersion
  lazy val TpolecatDep      = "org.typelevel"     % "sbt-tpolecat"       % TpolecatVersion
  lazy val UnidocDep        = "com.github.sbt"    % "sbt-unidoc"         % UnidocVersion
  lazy val UpdatesDep       = "com.timushev.sbt"  % "sbt-updates"        % UpdatesVersion

  lazy val sbtPlugins = Seq(
    ErrorsSummaryDep,
    MicrositesDep,
    TpolecatDep,
    UnidocDep,
    UpdatesDep,
  )

}
