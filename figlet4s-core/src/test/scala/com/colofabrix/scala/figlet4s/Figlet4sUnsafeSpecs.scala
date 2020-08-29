package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import scala.util._

class Figlet4sUnsafeSpecs extends AnyFlatSpec with Matchers {

  "Figlet4s" should "render a test case" in {
    val result = Figlet4s
      .builder(SpecsData.sampleStandard.input)
      .render()
      .asString()

    result should be(SpecsData.sampleStandard.expected)
  }

  it should "throw an exception" in {
    assertThrows[FigletLoadingError] {
      Figlet4s
        .builder(SpecsData.sampleStandard.input)
        .withFont("non_existent")
        .render()
        .asString()
    }
  }

  it should "return a list with the internal fonts" in {
    Figlet4s.internalFonts should contain("standard")
  }

  it should "load all internal fonts" in {
    val result = for {
      font  <- Figlet4s.internalFonts
      error <- interpretResult(font)(Try(Figlet4s.loadFontInternal(font)))
    } yield error

    result shouldBe empty
  }

  private def interpretResult(font: String): PartialFunction[Try[_], Option[String]] = {
    case Failure(fe @ FigletError(message)) =>
      Some(s"${fe.getClass().getSimpleName()} on $font: $message")
    case Failure(exception: Throwable) =>
      Some(s"Exception on $font: ${exception.getMessage}")
    case Success(_) =>
      None
  }
}
