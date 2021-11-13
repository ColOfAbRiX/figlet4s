package com.colofabrix.scala.figlet4s.either

import cats.scalatest._
import com.colofabrix.scala.figlet4s.StandardTestData._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class EitherFIGureSpecs extends AnyFlatSpec with Matchers with EitherMatchers with EitherValues {

  "FIGure" should "return the same data for asSeq() and asString()" in {
    val test =
      for {
        figure     <- standardBuilder.render(standardInput)
        fromSeq    <- figure.asSeqF()
        fromString <- figure.asStringF()
      } yield {
        (fromSeq.mkString(System.lineSeparator()), fromString)
      }

    test should be(right)

    val (computed, expected) = test.value
    computed should equal(expected)
  }

  it should "print the same data as asString()" in {
    val figure = standardBuilder.render(standardInput)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      figure.flatMap(_.print())
    }

    val computed = stream.toString()
    val expected = figure.map(_.asString() + System.lineSeparator()).value

    computed should equal(expected)
  }

}
