import CodeQualityDependencies._
import SbtPluginsDeps._
import PublishDependencies._

// Resolve version conflicts
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

// Add all dependencies
sbtPlugins.map(addSbtPlugin)
publishSbtPlugins.map(addSbtPlugin)
codeQualitySbtPlugins.map(addSbtPlugin)
