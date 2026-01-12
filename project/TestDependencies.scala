import sbt._

/**
 * Project dependencies
 */
object TestDependencies {

  lazy val CatsEffectTestingVersion = "1.6.0"
  lazy val ScalaCheckVersion        = "1.18.1"
  lazy val ScalaMockVersion         = "6.0.0"
  lazy val ScalaTestVersion         = "3.2.19"

  lazy val CatsScalaTestDep           = "org.typelevel" %% "cats-effect-testing-scalatest" % CatsEffectTestingVersion % "test,it"
  lazy val ScalaMockDep               = "org.scalamock" %% "scalamock"                     % ScalaMockVersion         % "test"
  lazy val ScalaTestDep               = "org.scalatest" %% "scalatest"                     % ScalaTestVersion         % "test,it"
  lazy val ScalaTestFlatSpecDep       = "org.scalatest" %% "scalatest-flatspec"            % ScalaTestVersion         % "test,it"
  lazy val ScalaTestShouldMatchersDep = "org.scalatest" %% "scalatest-shouldmatchers"      % ScalaTestVersion         % "test,it"

  //  Support  //

  private def scalatestpluscheck: String = {
    val major :: minor :: _ :: Nil = ScalaCheckVersion.split("\\.").toList: @unchecked
    s"scalacheck-$major-$minor"
  }

  lazy val ScalaTestPlusCheckDep = "org.scalatestplus" %% scalatestpluscheck % s"$ScalaTestVersion.0" % "it,test"

}
