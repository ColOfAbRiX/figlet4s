import sbt._

/**
 * Project dependencies
 */
object Dependencies {

  // Scala language version
  lazy val SupportedScalaLangVersion = List("2.13.3", "2.12.12")
  lazy val ScalaLangVersion          = SupportedScalaLangVersion.head

  //  Libraries  //

  lazy val CatsEffectVersion = "2.3.0"
  lazy val CatsVersion       = "2.3.0"
  lazy val EnumeratumVersion = "1.6.1"

  lazy val CatsCoreDep   = "org.typelevel" %% "cats-core"   % CatsVersion
  lazy val CatsEffectDep = "org.typelevel" %% "cats-effect" % CatsEffectVersion
  lazy val CatsKernelDep = "org.typelevel" %% "cats-kernel" % CatsVersion
  lazy val EnumeratumDep = "com.beachape"  %% "enumeratum"  % EnumeratumVersion % Compile

}
