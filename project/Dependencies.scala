import sbt._

/**
 * Project dependencies
 */
object Dependencies {

  // Scala language version
  lazy val SupportedScalaLangVersion = List("3.9.0", "2.13.18")
  lazy val ScalaLangVersion          = SupportedScalaLangVersion.head

  //  Libraries  //

  lazy val CatsEffectVersion = "3.7.1"
  lazy val CatsVersion       = "2.13.0"
  lazy val EnumeratumVersion = "1.9.8"

  lazy val CatsCoreDep   = "org.typelevel" %% "cats-core"   % CatsVersion
  lazy val CatsEffectDep = "org.typelevel" %% "cats-effect" % CatsEffectVersion
  lazy val CatsKernelDep = "org.typelevel" %% "cats-kernel" % CatsVersion
  lazy val EnumeratumDep = "com.beachape"  %% "enumeratum" % EnumeratumVersion % Compile
}
