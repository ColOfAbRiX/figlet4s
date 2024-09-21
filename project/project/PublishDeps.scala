import sbt._

/**
 * SBT plugins dependencies for Publishing
 */
object PublishDeps {

  lazy val AssemblyVersion          = "2.2.0"
  lazy val DynverVersion            = "5.0.1"
  lazy val ExplicitDepsVersion      = "0.3.1"
  lazy val GitVersion               = "2.0.1"
  lazy val GpgVersion               = "2.2.1"
  lazy val NativePackagerVersion    = "1.10.4"
  lazy val RemoveTestFromPomVersion = "0.1.0"
  lazy val SonatypeVersion          = "3.11.3"

  lazy val AssemblyDep          = "com.eed3si9n"         % "sbt-assembly"              % AssemblyVersion
  lazy val DynverDep            = "com.github.sbt"      %% "sbt-dynver"                % DynverVersion
  lazy val ExplicitDepsDep      = "com.github.cb372"     % "sbt-explicit-dependencies" % ExplicitDepsVersion
  lazy val GitDep               = "com.github.sbt"      %% "sbt-git"                   % GitVersion
  lazy val GpgDep               = "com.github.sbt"       % "sbt-pgp"                   % GpgVersion
  lazy val NativePackagerDep    = "com.github.sbt"      %% "sbt-native-packager"       % NativePackagerVersion
  lazy val RemoveTestFromPomDep = "com.alejandrohdezma" %% "sbt-remove-test-from-pom"  % RemoveTestFromPomVersion
  lazy val SonatypeDep          = "org.xerial.sbt"       % "sbt-sonatype"              % SonatypeVersion

  lazy val publishSbtPlugins = Seq(
    AssemblyDep,
    DynverDep,
    ExplicitDepsDep,
    GitDep,
    GpgDep,
    NativePackagerDep,
    RemoveTestFromPomDep,
    SonatypeDep,
  )

}
