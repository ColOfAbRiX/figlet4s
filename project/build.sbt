// SBT Sources
Compile / scalaSource := baseDirectory.value / "src"

// SBT Scala version
scalaVersion := "2.12.11"

//  VERSIONS  //

lazy val AssemblyVersion       = "0.15.0"
lazy val BuildinfoVersion      = "0.9.0"
lazy val DynverVersion         = "4.1.1"
lazy val ErrorsSummaryVersion  = "0.6.3"
lazy val ExplicitDepsVersion   = "0.2.15"
lazy val NativePackagerVersion = "1.7.5"
lazy val SbtStatsVersion       = "1.0.7"
lazy val ScalaFixVersion       = "0.9.21"
lazy val ScalafmtVersion       = "2.4.0"
lazy val UpdatesVersion        = "0.5.1"
lazy val WartremoverVersion    = "2.4.9"

//  PLUGIN LIBRARIES  //

addSbtPlugin("ch.epfl.scala"    % "sbt-scalafix"              % ScalaFixVersion)
addSbtPlugin("com.dwijnand"     % "sbt-dynver"                % DynverVersion)
addSbtPlugin("com.eed3si9n"     % "sbt-assembly"              % AssemblyVersion)
addSbtPlugin("com.eed3si9n"     % "sbt-buildinfo"             % BuildinfoVersion)
addSbtPlugin("com.github.cb372" % "sbt-explicit-dependencies" % ExplicitDepsVersion)
addSbtPlugin("com.orrsella"     % "sbt-stats"                 % SbtStatsVersion)
addSbtPlugin("com.timushev.sbt" % "sbt-updates"               % UpdatesVersion)
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager"       % NativePackagerVersion)
addSbtPlugin("org.duhemm"       % "sbt-errors-summary"        % ErrorsSummaryVersion)
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"              % ScalafmtVersion)
addSbtPlugin("org.wartremover"  % "sbt-wartremover"           % WartremoverVersion)
