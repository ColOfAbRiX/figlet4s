import sbt._

/**
 * Project dependencies
 */
object TestDependencies {

  lazy val CatsScalaTestVersion = "3.1.1"
  lazy val ScalaCheckVersion    = "1.15.0"
  lazy val ScalameterVersion    = "0.20"
  lazy val ScalaMockVersion     = "5.1.0"
  lazy val ScalaTestVersion     = "3.2.9"

  lazy val CatsScalaTestDep           = "com.ironcorelabs"  %% "cats-scalatest"           % CatsScalaTestVersion % Test
  lazy val ScalameterDep              = "com.storm-enroute" %% "scalameter"               % ScalameterVersion    % Test
  lazy val ScalaMockDep               = "org.scalamock"     %% "scalamock"                % ScalaMockVersion     % Test
  lazy val ScalaTestDep               = "org.scalatest"     %% "scalatest"                % ScalaTestVersion     % Test
  lazy val ScalaTestFlatSpecDep       = "org.scalatest"     %% "scalatest-flatspec"       % ScalaTestVersion     % Test
  lazy val ScalaTestShouldMatchersDep = "org.scalatest"     %% "scalatest-shouldmatchers" % ScalaTestVersion     % Test

  //  Support  //

  private def scalatestpluscheck: String = {
    val major :: minor :: _ :: Nil = ScalaCheckVersion.split("\\.").toList
    s"scalacheck-$major-$minor"
  }

  lazy val SonatypeRepos = Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
  )

  lazy val ScalaTestPlusCheckDep = "org.scalatestplus" %% scalatestpluscheck % s"$ScalaTestVersion.0" % Test

}
