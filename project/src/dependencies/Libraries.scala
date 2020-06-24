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
  lazy val PPrintVersion     = "0.5.9"

  // Generic
  lazy val CatsCoreDep   = "org.typelevel" %% "cats-core"   % CatsVersion
  lazy val CatsKernelDep = "org.typelevel" %% "cats-kernel" % CatsVersion
  lazy val EnumeratumDep = "com.beachape"  %% "enumeratum"  % EnumeratumVersion
  lazy val NewtypeDep    = "io.estatico"   %% "newtype"     % NewtypeVersion
  lazy val PPrintDep     = "com.lihaoyi"   %% "pprint"      % PPrintVersion

}
