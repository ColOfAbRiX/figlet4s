package com.colofabrix.scala.figlet4s

import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.testutils._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalacheck._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import org.scalatestplus.scalacheck._
import scala.util._

class Figlet4sUnsafeSpecs
    extends AnyFlatSpec
    with ScalaCheckDrivenPropertyChecks
    with Matchers
    with Figlet4sMatchers
    with OriginalFigletTesting {

  "Figlet4s" should "render a default test case" in {
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

  it should "return the list of internal fonts containing the \"standard\" font" in {
    Figlet4s.internalFonts should contain("standard")
  }

  it should "load all internal fonts successfully" in {
    val result = for {
      font  <- Figlet4s.internalFonts
      error <- interpretResult(font)(Try(Figlet4s.loadFontInternal(font)))
    } yield error

    result shouldBe empty
  }

  it should "render the texts as the original command line FIGlet does" in {
    figlet4sRenderingTest { text =>
      val computed = defaultBuilder.text(text).render()
      val expected = renderWithFiglet(defaultBuilder.options, text)
      computed should lookLike(expected)
    }
  }

  //  Support  //

  private def interpretResult(font: String): PartialFunction[Try[_], Option[String]] = {
    case Failure(fe @ FigletError(message)) =>
      Some(s"${fe.getClass().getSimpleName()} on $font: $message")
    case Failure(exception: Throwable) =>
      Some(s"Exception on $font: ${exception.getMessage}")
    case Success(_) =>
      None
  }

  private def figlet4sRenderingTest[A](f: String => A): Unit = {
    assumeExecutableInPath("figlet")
    forAll((figfontCharsGen, "renderText"), minSuccessful(50)) { text =>
      whenever(text.forall(FIGfont.requiredChars.contains(_))) {
        f(text)
      }
    }
  }

  private val figfontCharsGen: Gen[String] =
    Gen
      // .someOf(FIGfont.requiredChars)
      .someOf(Seq('='))
      .suchThat(x => x.length < 100)
      .map(Random.shuffle(_))
      .map(_.mkString)

  private val defaultBuilder =
    Figlet4s.builder().withInternalFont("standard")

}
