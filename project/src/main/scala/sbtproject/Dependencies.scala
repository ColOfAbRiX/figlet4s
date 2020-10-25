package sbtproject

import sbt._

/**
 * Project dependencies
 */
object Dependencies {

  // Scala language version
  lazy val ScalaLangVersion = "2.13.3"

  //  Libraries  //

  lazy val CatsEffectVersion = "2.2.0"
  lazy val CatsVersion       = "2.2.0"
  lazy val EnumeratumVersion = "1.6.1"

  lazy val CatsCoreDep   = "org.typelevel" %% "cats-core"   % CatsVersion
  lazy val CatsEffectDep = "org.typelevel" %% "cats-effect" % CatsEffectVersion
  lazy val CatsKernelDep = "org.typelevel" %% "cats-kernel" % CatsVersion
  lazy val EnumeratumDep = "com.beachape"  %% "enumeratum"  % EnumeratumVersion % Compile

  //  Testing  //

  lazy val ScalaCheckVersion = "1.14.1"
  lazy val ScalameterVersion = "0.19"
  lazy val ScalaTestVersion  = "3.2.2"

  lazy val ScalameterDep              = "com.storm-enroute" %% "scalameter"               % ScalameterVersion % Test
  lazy val ScalaTestDep               = "org.scalatest"     %% "scalatest"                % ScalaTestVersion  % Test
  lazy val ScalaTestFlatSpecDep       = "org.scalatest"     %% "scalatest-flatspec"       % ScalaTestVersion  % Test
  lazy val ScalaTestShouldMatchersDep = "org.scalatest"     %% "scalatest-shouldmatchers" % ScalaTestVersion  % Test

  //  Support  //

  private def scalatestpluscheck: String = {
    val major :: minor :: _ :: Nil = ScalaCheckVersion.split("\\.").toList
    s"scalacheck-$major-$minor"
  }

  lazy val SonatypeRepos = Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
  )

  lazy val ScalaTestPlusCheckDep = "org.scalatestplus" %% scalatestpluscheck % s"$ScalaTestVersion.0" % Test

}
