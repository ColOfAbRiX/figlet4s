package com.colofabrix.scala.figlet4s

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.catsio._
import com.colofabrix.scala.figlet4s.errors._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import com.colofabrix.scala.figlet4s.figfont.FIGfont

class Figlet4sCatsioSpecs extends AnyFlatSpec with Matchers {

  private def run[A](a: IO[A]): A = a.unsafeRunSync()

  "figlet4s-catsio" should "render a test case" in {
    val result = for {
      builder <- Figlet4s.builderF(SpecsData.sampleStandard.input)
      figure  <- builder.render()
      result  <- figure.asStringF()
    } yield result

    run(result) should equal(SpecsData.sampleStandard.expected)
  }

  it should "return an error inside IO" in {
    val result = for {
      builder <- Figlet4s.builderF("~ * Fao & C 123")
      figure  <- builder.withFont("non_existent").render()
      result  <- figure.asStringF()
    } yield result

    assertThrows[FigletLoadingError] {
      run(result)
    }
  }

  it should "return a list with the internal fonts" in {
    run(Figlet4s.internalFonts) should contain("standard")
  }

  it should "load all internal fonts" in {
    val result = for {
      font  <- Figlet4s.internalFonts.unsafeRunSync()
      error <- interpretResult(font)(Figlet4s.loadFontInternal(font))
    } yield error

    result shouldBe empty
  }

  private def interpretResult(font: String)(data: IO[FIGfont]): Option[String] = {
    data
      .map(_ => None: Option[String])
      .recover {
        case fe @ FigletError(message) =>
          Some(s"${fe.getClass().getSimpleName()} on $font: $message")
        case exception: Throwable =>
          Some(s"Exception on $font: ${exception.getMessage}")
      }
      .unsafeRunSync()
  }

}
