import sbt._
import CodeQualityDependencies._
import Plugins._
import PublishDependencies._

// Add all dependencies
sbtPlugins.map(addSbtPlugin)
publishSbtPlugins.map(addSbtPlugin)
codeQualitySbtPlugins.map(addSbtPlugin)
