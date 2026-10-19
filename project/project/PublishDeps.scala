import sbt._

/**
 * Publishing plugins dependencies
 */
object PublishDependencies {

  lazy val DynverVersion = "5.1.1"
  lazy val GitVersion    = "2.2.0"
  lazy val GpgVersion    = "2.3.2"

  lazy val DynverDep = "com.github.sbt" % "sbt-dynver" % DynverVersion
  lazy val GitDep    = "com.github.sbt" % "sbt-git"    % GitVersion
  lazy val GpgDep    = "com.github.sbt" % "sbt-pgp"    % GpgVersion

  lazy val publishSbtPlugins = Seq(
    DynverDep,
    GitDep,
    GpgDep,
  )
}
