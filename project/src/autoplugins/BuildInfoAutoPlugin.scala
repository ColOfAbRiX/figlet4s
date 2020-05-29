package autoplugins

import AllProjectsAutoPlugin.autoImport._
import sbt._
import sbt.Keys._
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport._

/**
 * Adds default settings for the BuildInfoPlugin
 */
object BuildInfoAutoPlugin extends AutoPlugin {
  override def requires: Plugins      = AllProjectsAutoPlugin && BuildInfoPlugin
  override def trigger: PluginTrigger = AllRequirements
  override def projectSettings: Seq[Setting[_]] = Seq(
    buildInfoPackage := projectPackage.value,
    buildInfoKeys ++= Seq[BuildInfoKey](
      "organization"   -> organization.value,
      "description"    -> description.value,
      "projectPackage" -> projectPackage.value,
    ),
  )
}
