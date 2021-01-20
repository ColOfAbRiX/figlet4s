import sbt._
import CodeQualityDependencies._
import Plugins._
import PublishDependencies._

// SBT Scala version
scalaVersion := "2.12.12"

// Add all dependencies
sbtPlugins.map(addSbtPlugin)
publishSbtPlugins.map(addSbtPlugin)
codeQualitySbtPlugins.map(addSbtPlugin)
