package com.colofabrix.scala.figlet4s.either

import com.colofabrix.scala.figlet4s.StandardTestData._
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class EitherFIGureSpecs extends AnyFlatSpec with Matchers with EitherValues {

  "FIGure" should "return the same data for asSeq() and asString()" in {
    val test =
      for {
        figure     <- standardBuilder.render(standardInput)
        fromSeq    <- figure.asSeqF()
        fromString <- figure.asStringF()
      } yield {
        (fromSeq.mkString("\n"), fromString)
      }

    val (computed, expected) = test.unsafeGet

    computed should equal(expected)
  }

  it should "print the same data as asString()" in {
    val figure = standardBuilder.render(standardInput)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      figure.flatMap(_.print())
    }

    val computed = stream.toString()
    val expected = figure.map(_.asString() + "\n").unsafeGet

    computed should equal(expected)
  }

}
