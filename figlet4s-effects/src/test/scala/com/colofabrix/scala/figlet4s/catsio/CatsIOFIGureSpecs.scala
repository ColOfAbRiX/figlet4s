package com.colofabrix.scala.figlet4s.catsio

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.colofabrix.scala.figlet4s.StandardTestData._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class CatsIOFIGureSpecs extends AnyFlatSpec with Matchers {

  private def run[A](a: IO[A]): A = a.unsafeRunSync()

  "FIGure" should "return the same data for asSeq() and asString()" in {
    val test =
      for {
        figure     <- standardBuilder.render(standardInput)
        fromSeq    <- figure.asSeqF()
        fromString <- figure.asStringF()
      } yield {
        fromSeq.mkString(System.lineSeparator()) should equal(fromString)
      }
    run(test)
  }

  it should "return one String per displayable line, matching the unsafe and either APIs" in {
    val expected = standardLines.value

    val actual = run(standardBuilder.render(standardInput).flatMap(_.asSeqF()))

    actual should equal(expected)
  }

  it should "return one String per displayable line when the text wraps on multiple bands" in {
    val (figure, actual) =
      run(
        standardBuilder
          .withMaxWidth(10)
          .render(standardInput)
          .flatMap(figure => figure.asSeqF().map(rows => (figure, rows))),
      )

    actual should equal(figure.cleanLines.flatMap(_.value))
    actual.foreach(_ should not include System.lineSeparator())
  }

  it should "return the same data for the bare asSeq()/asString() as their F-wrapped versions" in {
    val figure        = run(standardBuilder.render(standardInput))
    val bareSeq       = figure.asSeq()
    val bareString    = figure.asString()
    val wrappedSeq    = run(figure.asSeqF())
    val wrappedString = run(figure.asStringF())

    bareSeq should equal(wrappedSeq)
    bareString should equal(wrappedString)
  }

  it should "print the same data as asString()" in {
    val figure      = standardBuilder.render(standardInput)
    val stream      = new java.io.ByteArrayOutputStream()
    val printStream = new java.io.PrintStream(stream)

    val oldOut = System.out
    try {
      System.setOut(printStream)
      figure.flatMap(_.print()).unsafeRunSync()
    } finally {
      System.setOut(oldOut)
    }

    val computed = stream.toString()
    val expected = run(figure.map(_.asString() + System.lineSeparator()))

    computed should equal(expected)
  }

}
