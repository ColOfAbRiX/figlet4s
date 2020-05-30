package dependencies

import sbt._

/**
 * Libraries
 */
trait Libraries {

  // Versions
  lazy val CatsVersion          = "2.1.1"
  lazy val EnumeratumVersion    = "1.6.0"
  lazy val PPrintVersion        = "0.5.9"
  lazy val ScalaCheckVersion    = "1.14.1"
  lazy val ScalaTestCatsVersion = "3.0.5"
  lazy val ScalaTestVersion     = "3.1.1"
  lazy val ShapelessVersion     = "2.3.3"

  // Generic
  lazy val CatsCoreDep       = "org.typelevel"    %% "cats-core"       % CatsVersion
  lazy val CatsEffectDep     = "org.typelevel"    %% "cats-effect"     % CatsVersion
  lazy val EnumeratumCatsDep = "com.beachape"     %% "enumeratum-cats" % EnumeratumVersion
  lazy val EnumeratumDep     = "com.beachape"     %% "enumeratum"      % EnumeratumVersion
  lazy val PPrintDep         = "com.lihaoyi"      %% "pprint"          % PPrintVersion
  lazy val ScalaCheckDep     = "org.scalacheck"   %% "scalacheck"      % ScalaCheckVersion % Test
  lazy val ScalaTestCatsDep  = "com.ironcorelabs" %% "cats-scalatest"  % ScalaTestCatsVersion % Test
  lazy val ScalaTestDep      = "org.scalatest"    %% "scalatest"       % ScalaTestVersion % Test
  lazy val ShapelessDep      = "com.chuusai"      %% "shapeless"       % ShapelessVersion

}
