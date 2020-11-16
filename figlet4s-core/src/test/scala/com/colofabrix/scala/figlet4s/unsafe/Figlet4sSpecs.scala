package com.colofabrix.scala.figlet4s.unsafe

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.StandardTestData._
import com.colofabrix.scala.figlet4s.testutils._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import scala.concurrent.ExecutionContext
import scala.util._

class Figlet4sSpecs extends AnyFlatSpec with Matchers with Figlet4sMatchers with OriginalFigletTesting {

  //  Rendering  //

  "Rendering APIs" should "render a default text using the \"standard\" font" in {
    val computed = Figlet4s.renderString(standardInput, standardBuilder.options)
    val expected = FIGure(standardBuilder.options.font, standardInput, Vector(standardLines.toSubcolumns))
    computed should lookLike(expected)
  }

  it should "render the texts as the original command line FIGlet does" taggedAs (SlowTest) in {
    figletRenderingTest { testData =>
      val testBuilder =
        Figlet4s
          .builder()
          .text(testData.renderText)
          .withInternalFont(testData.fontName)
          .withHorizontalLayout(testData.horizontalLayout)
          .withPrintDirection(testData.printDirection)
          .withJustification(testData.justification)

      val computed = testBuilder.render()
      val expected = renderWithFiglet(testBuilder.options, testData.renderText)

      computed should lookLike(expected)
    }
  }

  //  Internal fonts  //

  "Internal Fonts API" should "return the list of internal fonts containing at least the \"standard\" font" in {
    Figlet4s.internalFonts should contain(Figlet4sClient.defaultFont)
  }

  it should "load all internal fonts successfully" in {
    val loadingErrors = for {
      fonts <- Figlet4s.internalFonts
      error <- interpretResult(fonts)(Try(Figlet4s.loadFontInternal(fonts)))
    } yield error
    loadingErrors shouldBe empty
  }

  it should "support loading of internal fonts in parallel" in {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val data = Figlet4s
      .internalFonts
      .toList
      .take(10)
      .flatMap(Seq.fill(10)(_))

    val test = data.parTraverse { fontName =>
      IO(Figlet4s.loadFontInternal(fontName))
    }

    test.unsafeRunSync()
  }

  it should "throw a FigletLoadingError when trying to load a font that doesn't exist" in {
    assertThrows[FigletLoadingError] {
      Figlet4s.loadFontInternal("non_existent")
    }
  }

  //  Fonts  //

  "Fonts API" should "read a font from the file system" in {
    val cwd    = System.getProperty("user.dir")
    val font   = Try(Figlet4s.loadFont(s"$cwd/figlet4s-core/src/main/resources/fonts/standard.flf"))
    val result = interpretResult(Figlet4sClient.defaultFont)(Try(font))
    result shouldBe empty
  }

  it should "throw a FigletLoadingError when trying to load a font that doesn't exist" in {
    assertThrows[FigletLoadingError] {
      Figlet4s.loadFont("non_existent")
    }
  }

  //  Support  //

  private def interpretResult(font: String): PartialFunction[Try[_], Option[String]] = {
    case Failure(fe @ FigletException(message)) =>
      Some(s"${fe.getClass.getSimpleName} on $font: $message")
    case Failure(exception: Throwable) =>
      Some(s"Exception on $font: ${exception.getMessage}")
    case Success(_) =>
      None
  }

}
