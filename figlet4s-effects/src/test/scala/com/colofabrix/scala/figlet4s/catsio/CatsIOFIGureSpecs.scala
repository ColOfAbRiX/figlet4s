package com.colofabrix.scala.figlet4s.catsio

import cats.effect.IO
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
        fromSeq.mkString("\n") should equal(fromString)
      }
   run(test)
  }

  it should "print the same data as asString()" in {
    val figure = standardBuilder.render(standardInput)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      figure.flatMap(_.print()).unsafeRunSync()
    }

    val computed = stream.toString()
    val expected = run(figure.map(_.asString() + "\n"))

    computed should equal(expected)
  }

}
