package com.colofabrix.scala.figlet4s

import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import com.colofabrix.scala.figlet4s.unsafe._

class Figlet4sUnsafeSpecs extends AnyFlatSpec with Matchers {

  "figlet4s" should "render a test case" in {
    Figlet4s
      .builder("~ * Fao & C 123")
      .render()
      .print()
  }

  it should "throw an exception" in {
    assertThrows[Throwable] {
      Figlet4s
        .builder("~ * Fao & C 123")
        .withFont("non_existent")
        .render()
        .print()
    }
  }

}
