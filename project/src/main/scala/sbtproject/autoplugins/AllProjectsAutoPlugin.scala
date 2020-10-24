package sbtproject.autoplugins

import sbt._
import sbt.Keys._
import sbt.librarymanagement.ModuleID

/**
 * Adds default settings for the BuildInfoPlugin
 * For some instructions see:
 *   https://amirkarimi.me/2017/05/17/how-to-apply-settings-to-multiple-projects-using-sbt-triggered-plugins.html
 */
object AllProjectsAutoPlugin extends AutoPlugin {
  import autoImport._

  object autoImport {
    val projectPackage      = settingKey[String]("The root package of a project")
    val bundledDependencies = settingKey[Seq[Seq[ModuleID]]]("Declares managed dependencies as collection of bundles.")
  }

  override def trigger: PluginTrigger           = AllRequirements
  override def globalSettings: Seq[Setting[_]]  = Seq(
    projectPackage := (ThisBuild / organization).value,
    bundledDependencies := Seq(),
  )
  override def projectSettings: Seq[Setting[_]] = Seq(
    bundledDependencies := Seq(),
    libraryDependencies ++= bundledDependencies.value.flatten,
    projectPackage := organization.value,
  )
}
