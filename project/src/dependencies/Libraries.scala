package dependencies

import sbt._

/**
 * Libraries
 */
trait Libraries {

  // Versions
  lazy val CatsEffectVersion = "2.1.4"
  lazy val CatsVersion       = "2.1.1"
  lazy val EnumeratumVersion = "1.6.1"
  lazy val ScalameterVersion = "0.19"
  lazy val ScalaTestVersion  = "3.2.2"

  // Repositories
  lazy val SonatypeRepo = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

  // Libraries
  lazy val CatsCoreDep                = "org.typelevel"     %% "cats-core"                % CatsVersion
  lazy val CatsEffectDep              = "org.typelevel"     %% "cats-effect"              % CatsEffectVersion
  lazy val CatsKernelDep              = "org.typelevel"     %% "cats-kernel"              % CatsVersion
  lazy val EnumeratumDep              = "com.beachape"      %% "enumeratum"               % EnumeratumVersion % Compile
  lazy val ScalameterDep              = "com.storm-enroute" %% "scalameter"               % ScalameterVersion % Test
  lazy val ScalaTestFlatSpecDep       = "org.scalatest"     %% "scalatest-flatspec"       % ScalaTestVersion  % Test
  lazy val ScalaTestShouldMatchersDep = "org.scalatest"     %% "scalatest-shouldmatchers" % ScalaTestVersion  % Test

}
