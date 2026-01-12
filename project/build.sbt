import sbt.{Plugins => _, _}
import CodeQualityDependencies._
import Plugins._
import PublishDependencies._

// Resolve version conflicts
ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)

// Add all dependencies
sbtPlugins.map(addSbtPlugin)
publishSbtPlugins.map(addSbtPlugin)
codeQualitySbtPlugins.map(addSbtPlugin)
