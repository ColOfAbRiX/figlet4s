import CodeQualityDeps.*
import PluginDeps.*
import PublishDeps.*
import sbt.*

//ThisBuild / evictionErrorLevel := Level.Info
ThisBuild / libraryDependencySchemes ++= Seq(
  "com.lihaoyi"            %% "geny"      % VersionScheme.Always,
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always,
)

// Add all dependencies
codeQualitySbtPlugins.map(addSbtPlugin)
publishSbtPlugins.map(addSbtPlugin)
sbtPlugins.map(addSbtPlugin)
