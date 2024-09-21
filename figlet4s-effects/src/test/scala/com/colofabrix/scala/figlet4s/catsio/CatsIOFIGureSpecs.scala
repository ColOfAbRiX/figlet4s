package com.colofabrix.scala.figlet4s.catsio

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.colofabrix.scala.figlet4s.StandardTestData._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class CatsIOFIGureSpecs extends AsyncFlatSpec with AsyncIOSpec with Matchers {

  "FIGure" should "return the same data for asSeq() and asString()" in {
    for {
      figure     <- standardBuilder.render(standardInput)
      fromSeq    <- figure.asSeqF()
      fromString <- figure.asStringF()
    } yield {
      fromSeq.mkString(System.lineSeparator()) should equal(fromString)
    }
  }

  it should "print the same data as asString()" ignore {
    for {
      figure <- standardBuilder.render(standardInput)
      stream   = new java.io.ByteArrayOutputStream()
      _        = Console.withOut(stream)(figure.print().unsafeRunSync())
      computed = stream.toString()
      expected = figure.asString() + System.lineSeparator()
    } yield {
      computed shouldBe expected
    }
  }

}
