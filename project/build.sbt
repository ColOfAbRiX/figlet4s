import sbt._
import sbtproject.CodeQualityDependencies._
import sbtproject.Dependencies._
import sbtproject.PublishDependencies._

// SBT Sources
Compile / scalaSource := baseDirectory.value / "src/main/scala"

// SBT Scala version
scalaVersion := "2.12.12"

// Add all dependencies
sbtPlugins.map(addSbtPlugin)
publishSbtPlugins.map(addSbtPlugin)
codeQualitySbtPlugins.map(addSbtPlugin)
