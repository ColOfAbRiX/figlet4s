package com.colofabrix.scala.figlet4s

import org.scalatest.flatspec._
import org.scalatest.matchers.should
import com.colofabrix.scala.figlet4s.unsafe._

class Figlet4sUnsafeSpecs extends AnyFlatSpec with should.Matchers {

  "figlet4s" should "render a test case" in {
    Figlet4s
      .builder("~ * Fao & C 123")
      .render()
      .print()
  }

}
