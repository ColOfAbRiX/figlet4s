package com.colofabrix.scala.figlet4s.unsafe

import com.colofabrix.scala.figlet4s.testutils._
import com.colofabrix.scala.figlet4s.StandardTestData._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class UnsafeFIGureSpecs extends AnyFlatSpec with Matchers with Figlet4sMatchers with OriginalFigletTesting {

  "FIGure" should "return the same data for asSeq() and asString()" in {
    val figure     = standardBuilder.render(standardInput)
    val fromSeq    = figure.asSeq().mkString("\n")
    val fromString = figure.asString()
    fromSeq should equal(fromString)
  }

  it should "print the same data as asString()" in {
    val figure = standardBuilder.render(standardInput)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      figure.print()
    }

    val computed = stream.toString()
    val expected = figure.asString() + "\n"

    computed should equal(expected)
  }

}
