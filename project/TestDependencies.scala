import sbt.*

/**
 * Project dependencies
 */
object TestDependencies {

  lazy val ScalaTestCatsV       = "3.1.1"
  lazy val ScalaTestCatsEffectV = "1.5.0"
  lazy val ScalaCheckV          = "1.18.0"
  lazy val ScalaMeterV          = "0.21"
  lazy val ScalaMockV           = "6.0.0"
  lazy val ScalaTestV           = "3.2.19"

  lazy val ScalaMeterDep              = "com.storm-enroute" %% "scalameter"                    % ScalaMeterV          % Test
  lazy val ScalaMockDep               = "org.scalamock"     %% "scalamock"                     % ScalaMockV           % Test
  lazy val ScalaTestCatsDep           = "com.ironcorelabs"  %% "cats-scalatest"                % ScalaTestCatsV       % Test
  lazy val ScalatestCatsEffectDep     = "org.typelevel"     %% "cats-effect-testing-scalatest" % ScalaTestCatsEffectV % Test
  lazy val ScalaTestDep               = "org.scalatest"     %% "scalatest"                     % ScalaTestV           % Test
  lazy val ScalaTestFlatSpecDep       = "org.scalatest"     %% "scalatest-flatspec"            % ScalaTestV           % Test
  lazy val ScalaTestShouldMatchersDep = "org.scalatest"     %% "scalatest-shouldmatchers"      % ScalaTestV           % Test

  //  Support  //

  private def scalatestpluscheck: String = {
    val major :: minor :: _ :: Nil = ScalaCheckV.split("\\.").toList
    s"scalacheck-$major-$minor"
  }

  lazy val SonatypeRepos = Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
  )

  lazy val ScalaTestPlusCheckDep = "org.scalatestplus" %% scalatestpluscheck % s"$ScalaTestV.0" % Test

}
