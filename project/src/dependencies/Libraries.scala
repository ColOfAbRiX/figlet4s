package dependencies

import sbt._

/**
 * Libraries
 */
trait Libraries {

  // Versions
  lazy val CatsVersion       = "2.1.1"
  lazy val EnumeratumVersion = "1.6.0"
  lazy val PPrintVersion     = "0.5.9"

  // Generic
  lazy val CatsCoreDep   = "org.typelevel" %% "cats-core"   % CatsVersion
  lazy val CatsEffectDep = "org.typelevel" %% "cats-effect" % CatsVersion
  lazy val EnumeratumDep = "com.beachape"  %% "enumeratum"  % EnumeratumVersion
  lazy val PPrintDep     = "com.lihaoyi"   %% "pprint"      % PPrintVersion

}
