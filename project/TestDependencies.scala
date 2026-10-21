import sbt._

/**
 * Project dependencies
 */
object TestDependencies {

  lazy val CatsEffectTestingVersion = "1.8.0"
  lazy val CatsScalaTestVersion2    = "3.1.1" // For Scala 2.13
  lazy val CatsScalaTestVersion3    = "4.0.2" // For Scala 3
  lazy val ScalaCheckEffectVersion  = "2.1.0"
  lazy val ScalaCheckVersion        = "1.20.0"
  lazy val ScalaMockVersion         = "7.6.0"
  lazy val ScalaTestVersion         = "3.2.20"

  lazy val CatsEffectTestingDep       = "org.typelevel" %% "cats-effect-testing-scalatest" % CatsEffectTestingVersion % "test"
  lazy val ScalaCheckEffectDep        = "org.typelevel" %% "scalacheck-effect"            % ScalaCheckEffectVersion  % "test"
  lazy val ScalaMockDep               = "org.scalamock"     %% "scalamock"                % ScalaMockVersion       % "test"
  lazy val ScalaMockScalaTestDep      = "org.scalamock"     %% "scalamock-scalatest"      % ScalaMockVersion       % "test"
  lazy val ScalaTestDep               = "org.scalatest"     %% "scalatest"                % ScalaTestVersion       % "test"
  lazy val ScalaTestFlatSpecDep       = "org.scalatest"     %% "scalatest-flatspec"       % ScalaTestVersion       % "test"
  lazy val ScalaTestPlusCheckDep      = "org.scalatestplus" %% scalatestpluscheck         % s"$ScalaTestVersion.0" % "test"
  lazy val ScalaTestShouldMatchersDep = "org.scalatest"     %% "scalatest-shouldmatchers" % ScalaTestVersion       % "test"

  //  Support  //

  private def scalatestpluscheck: String = {
    val major :: minor :: _ :: Nil = ScalaCheckVersion.split("\\.").toList: @unchecked
    s"scalacheck-$major-$minor"
  }

  def catsScalaTestDep(scalaVersion: String): ModuleID = {
    val version = if (scalaVersion.startsWith("2.")) CatsScalaTestVersion2 else CatsScalaTestVersion3
    "com.ironcorelabs" %% "cats-scalatest" % version % "test"
  }

}
