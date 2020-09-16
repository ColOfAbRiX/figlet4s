package settings

import sbt._

object Scopes {

  lazy val Benchmark =
    config("bench")
      .extend(Test)
      .describedAs("Configuration for Benchmarking")

}
