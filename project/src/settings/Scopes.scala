package settings

import sbt._

object Scopes {

  lazy val Bench     = "bench"
  lazy val Benchmark = config(Bench) extend Test

}
