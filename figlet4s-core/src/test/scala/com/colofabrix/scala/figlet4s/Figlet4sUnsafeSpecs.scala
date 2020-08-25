package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class Figlet4sUnsafeSpecs extends AnyFlatSpec with Matchers {

  "figlet4s" should "render a test case" in {
    val result = Figlet4s
      .builder(SpecsData.sampleStandard.input)
      .render()
      .asString()

    result should be(SpecsData.sampleStandard.expected)
  }

  it should "throw an exception" in {
    assertThrows[FigletLoadingError] {
      Figlet4s
        .builder("~ * Fao & C 123")
        .withFont("non_existent")
        .render()
        .asString()
    }
  }

  it should "return a list with the internal fonts" in {
    Figlet4s.internalFonts should contain("standard")
  }
}
