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

  // Libraries
  lazy val CatsCoreDep   = "org.typelevel" %% "cats-core"   % CatsVersion
  lazy val CatsEffectDep = "org.typelevel" %% "cats-effect" % CatsEffectVersion
  lazy val CatsFreeDep   = "org.typelevel" %% "cats-free"   % CatsVersion
  lazy val CatsKernelDep = "org.typelevel" %% "cats-kernel" % CatsVersion
  lazy val EnumeratumDep = "com.beachape"  %% "enumeratum"  % EnumeratumVersion

}
