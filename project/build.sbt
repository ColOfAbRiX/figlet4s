import sbt._
import sbtproject.CodeQualityDependencies._
import sbtproject.Dependencies._
import sbtproject.PublishDependencies._

// SBT Sources
Compile / scalaSource := baseDirectory.value / "src/main/scala"

// SBT Scala version
scalaVersion := "2.12.12"

// Add all dependencies
sbtPlugins.map(addSbtPlugin(_))
publishSbtPlugins.map(addSbtPlugin(_))
codeQualitySbtPlugins.map(addSbtPlugin(_))
