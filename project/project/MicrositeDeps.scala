import sbt._

/**
 * Microsite plugins dependencies
 */
object MicrositeDependencies {

  lazy val SiteVersion        = "1.8.0"
  lazy val SiteParadoxVersion = "1.8.0"
  lazy val ParadoxVersion     = "0.11.1"

  lazy val SiteDep        = "com.github.sbt" % "sbt-site"        % SiteVersion
  lazy val SiteParadoxDep = "com.github.sbt" % "sbt-site-paradox" % SiteParadoxVersion
  lazy val ParadoxDep     = "com.lightbend.paradox" % "sbt-paradox" % ParadoxVersion

  lazy val micrositeSbtPlugins = Seq(
    SiteDep,
    SiteParadoxDep,
    ParadoxDep,
  )
}
