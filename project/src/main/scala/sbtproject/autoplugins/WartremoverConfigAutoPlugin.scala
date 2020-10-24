//package sbtproject.autoplugins
//
//import sbt._
//import sbt.Keys._
//import sbtproject.dependencies.AllDependencies.WartremoverPlugin
//import wartremover.WartRemover.autoImport._
//
///**
// * Sets nice compiler settings
// */
//object WartremoverConfigAutoPlugin extends AutoPlugin {
//  override def requires: Plugins                = plugins.JvmPlugin
//  override def trigger: PluginTrigger           = AllRequirements
//  override def projectSettings: Seq[Setting[_]] = Seq(
//    libraryDependencies ++= Seq(WartremoverPlugin),
//    Compile / compile / wartremoverErrors := Warts.allBut(
//      Wart.Any,
//      Wart.DefaultArguments,
//      Wart.Nothing,
//      Wart.Overloading,
//      Wart.StringPlusAny,
//      Wart.ToString,
//      // Covered by ScalaFix
//      Wart.PublicInference
//    ),
//    Compile / compile / wartremoverWarnings := Seq.empty,
//    Test / compile / wartremoverErrors := Seq.empty,
//    Test / compile / wartremoverWarnings := (Compile / wartremoverErrors).value
//  )
//}
