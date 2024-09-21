import sbt.*

/**
 * Project dependencies
 */
object Dependencies {

  // Scala language version
  lazy val SupportedScalaLangVersion = List("2.13.14", "2.12.19")
  lazy val ScalaLangVersion          = SupportedScalaLangVersion.head

  //  Libraries  //

  lazy val CatsEffectVersion = "3.5.4"
  lazy val CatsVersion       = "2.12.0"
  lazy val EnumeratumVersion = "1.7.4"

  lazy val CatsCoreDep   = "org.typelevel" %% "cats-core"   % CatsVersion
  lazy val CatsEffectDep = "org.typelevel" %% "cats-effect" % CatsEffectVersion
  lazy val CatsKernelDep = "org.typelevel" %% "cats-kernel" % CatsVersion
  lazy val EnumeratumDep = "com.beachape"  %% "enumeratum"  % EnumeratumVersion % Compile

}
