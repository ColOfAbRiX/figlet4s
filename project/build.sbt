// SBT Sources
Compile / scalaSource := baseDirectory.value / "src/main/scala"

// SBT Scala version
scalaVersion := "2.12.11"

//  VERSIONS  //

lazy val ErrorsSummaryVersion = "0.6.3"
lazy val SbtStatsVersion      = "1.0.7"
lazy val UnidocVersion        = "0.4.3"
lazy val UpdatesVersion       = "0.5.1"

// Code quality
lazy val ExplicitDepsVersion = "0.2.15"
lazy val ScalaFixVersion     = "0.9.21"
lazy val ScalafmtVersion     = "2.4.0"
lazy val SCoverageVersion    = "1.6.1"
lazy val WartremoverVersion  = "2.4.10"

// Release
lazy val AssemblyVersion       = "0.15.0"
lazy val DynverVersion         = "4.1.1"
lazy val GpgVersion            = "2.0.1"
lazy val NativePackagerVersion = "1.7.5"
lazy val ReleaseVersion        = "1.0.13"
lazy val SonatypeVersion       = "3.9.5"

//  PLUGIN LIBRARIES  //

addSbtPlugin("com.eed3si9n"     % "sbt-unidoc"         % UnidocVersion)
addSbtPlugin("com.orrsella"     % "sbt-stats"          % SbtStatsVersion)
addSbtPlugin("com.timushev.sbt" % "sbt-updates"        % UpdatesVersion)
addSbtPlugin("org.duhemm"       % "sbt-errors-summary" % ErrorsSummaryVersion)

// Code quality
addSbtPlugin("ch.epfl.scala"    % "sbt-scalafix"              % ScalaFixVersion)
addSbtPlugin("com.github.cb372" % "sbt-explicit-dependencies" % ExplicitDepsVersion)
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"              % ScalafmtVersion)
addSbtPlugin("org.scoverage"    % "sbt-scoverage"             % SCoverageVersion)
addSbtPlugin("org.wartremover"  % "sbt-wartremover"           % WartremoverVersion)

// Release
addSbtPlugin("com.eed3si9n"      % "sbt-assembly"        % AssemblyVersion)
addSbtPlugin("com.dwijnand"      % "sbt-dynver"          % DynverVersion)
addSbtPlugin("com.github.gseitz" % "sbt-release"         % ReleaseVersion)
addSbtPlugin("com.jsuereth"      % "sbt-pgp"             % GpgVersion)
addSbtPlugin("com.typesafe.sbt"  % "sbt-native-packager" % NativePackagerVersion)
addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype"        % SonatypeVersion)
