import sbt._

/**
 * SBT plugins dependencies for Publishing
 */
object PublishDependencies {

  lazy val AssemblyVersion          = "1.1.0"
  lazy val DynverVersion            = "4.1.1"
  lazy val ExplicitDepsVersion      = "0.2.16"
  lazy val GitVersion               = "1.0.2"
  lazy val GpgVersion               = "2.1.2"
  lazy val NativePackagerVersion    = "1.7.5"
  lazy val RemoveTestFromPomVersion = "0.1.0"
  lazy val SonatypeVersion          = "3.9.7"

  lazy val AssemblyDep          = "com.eed3si9n"         % "sbt-assembly"              % AssemblyVersion
  lazy val DynverDep            = "com.dwijnand"         % "sbt-dynver"                % DynverVersion
  lazy val ExplicitDepsDep      = "com.github.cb372"     % "sbt-explicit-dependencies" % ExplicitDepsVersion
  lazy val GitDep               = "com.typesafe.sbt"     % "sbt-git"                   % GitVersion
  lazy val GpgDep               = "com.github.sbt"       % "sbt-pgp"                   % GpgVersion
  lazy val NativePackagerDep    = "com.typesafe.sbt"     % "sbt-native-packager"       % NativePackagerVersion
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
