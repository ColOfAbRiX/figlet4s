package sbtproject.settings

import sbt._

object Configurations {

  lazy val Benchmark =
    config("benchmark")
      .extend(Test)
      .describedAs("Configuration for Benchmarking")

}
