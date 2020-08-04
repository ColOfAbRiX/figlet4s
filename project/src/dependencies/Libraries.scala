package dependencies

import sbt._

/**
 * Libraries
 */
trait Libraries {

  // Versions
  lazy val CatsVersion       = "2.1.1"
  lazy val EnumeratumVersion = "1.6.1"
  lazy val NewtypeVersion    = "0.4.4"

  // Generic
  lazy val CatsCoreDep   = "org.typelevel" %% "cats-core"   % CatsVersion
  lazy val CatsEffectDep = "org.typelevel" %% "cats-effect" % CatsVersion
  lazy val CatsFreeDep   = "org.typelevel" %% "cats-free"   % CatsVersion
  lazy val CatsKernelDep = "org.typelevel" %% "cats-kernel" % CatsVersion
  lazy val EnumeratumDep = "com.beachape"  %% "enumeratum"  % EnumeratumVersion

}
