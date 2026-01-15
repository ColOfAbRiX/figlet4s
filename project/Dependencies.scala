import sbt._

/**
 * Project dependencies
 */
object Dependencies {

  // Scala language version
  lazy val SupportedScalaLangVersion = List("3.6.3", "2.13.16")
  lazy val ScalaLangVersion          = SupportedScalaLangVersion.head

  //  Libraries  //

  lazy val CatsEffectVersion = "3.6.3"
  lazy val CatsVersion       = "2.13.0"
  lazy val EnumeratumVersion = "1.7.6"

  lazy val CatsCoreDep   = "org.typelevel" %% "cats-core"   % CatsVersion
  lazy val CatsEffectDep = "org.typelevel" %% "cats-effect" % CatsEffectVersion
  lazy val CatsKernelDep = "org.typelevel" %% "cats-kernel" % CatsVersion
  lazy val EnumeratumDep = "com.beachape"  %% "enumeratum"  % EnumeratumVersion % Compile

}
