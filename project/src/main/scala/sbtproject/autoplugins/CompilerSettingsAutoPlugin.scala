//package sbtproject.autoplugins
//
//import sbt.Keys._
//import sbt._
//import sbtproject._
//import sbtproject.dependencies.AllDependencies.SplainPlugin
//
///**
// * Sets nice compiler settings
// */
//object CompilerSettingsAutoPlugin extends AutoPlugin {
//  override def requires: Plugins                = plugins.JvmPlugin
//  override def trigger: PluginTrigger           = AllRequirements
//  override def projectSettings: Seq[Setting[_]] = Seq(
//    libraryDependencies ++= Seq(SplainPlugin),
//    Compile / scalacOptions := Compiler.TpolecatOptions_2_13 ++ Compiler.StrictOptions,
//    Test / scalacOptions := Compiler.TpolecatOptions_2_13
//  )
//}
