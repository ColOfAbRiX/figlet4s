package dependencies

import sbt._

/**
 * Libraries
 */
trait Libraries {

  lazy val CatsEffectVersion = "2.1.4"
  lazy val CatsVersion       = "2.1.1"
  lazy val EnumeratumVersion = "1.6.1"

  lazy val CatsCoreDep   = "org.typelevel" %% "cats-core"   % CatsVersion
  lazy val CatsEffectDep = "org.typelevel" %% "cats-effect" % CatsEffectVersion
  lazy val CatsKernelDep = "org.typelevel" %% "cats-kernel" % CatsVersion
  lazy val EnumeratumDep = "com.beachape"  %% "enumeratum"  % EnumeratumVersion % Compile

  //  Testing  //

  lazy val ScalaCheckVersion = "1.14.1"
  lazy val ScalameterVersion = "0.19"
  lazy val ScalaTestVersion  = "3.2.2"

  lazy val SonatypeRepo = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

  lazy val ScalameterDep              = "com.storm-enroute" %% "scalameter"               % ScalameterVersion % Test
  lazy val ScalaTestDep               = "org.scalatest"     %% "scalatest"                % ScalaTestVersion  % Test
  lazy val ScalaTestFlatSpecDep       = "org.scalatest"     %% "scalatest-flatspec"       % ScalaTestVersion  % Test
  lazy val ScalaTestShouldMatchersDep = "org.scalatest"     %% "scalatest-shouldmatchers" % ScalaTestVersion  % Test

  private def version(string: String): (String, String, String) =
    string.split("\\.").toList match {
      case List(a, b, c) => (a, b, c)
    }

  lazy val ScalaTestPlusCheckVersion = "2.0"
  lazy val ScalaTestPlusCheckDep =
    "org.scalatestplus" %%
    s"scalacheck-${version(ScalaCheckVersion)._1}-${version(ScalaCheckVersion)._2}"                %
    s"${version(ScalaTestVersion)._1}.${version(ScalaTestVersion)._2}.${ScalaTestPlusCheckVersion}" % Test

}
