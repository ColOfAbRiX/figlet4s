package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.either._
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should.Matchers

class Figlet4sEitherSpecs extends AnyFlatSpec with Matchers with EitherValues {

  "figlet4s-either" should "render a test case and return Right()" in {
    val result =
      Figlet4s
        .builder(SpecsData.sampleStandard.input)
        .render()
        .flatMap(_.asStringF())

    result should be(Right(SpecsData.sampleStandard.expected))
  }

  it should "return an error inside Left()" in {
    val result =
      Figlet4s
        .builder("~ * Fao & C 123")
        .withFont("non_existent")
        .render()
        .map(_.asString())

    result.isLeft should be(true)
  }

  it should "return a list with the internal fonts" in {
    Figlet4s.internalFonts.getOrElse(Vector.empty) should contain("standard")
  }
}
