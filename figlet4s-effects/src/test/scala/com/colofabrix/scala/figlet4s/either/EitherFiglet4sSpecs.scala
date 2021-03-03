package com.colofabrix.scala.figlet4s.either

import cats.scalatest._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.StandardTestData._
import com.colofabrix.scala.figlet4s.testutils.Figlet4sMatchers
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import scala.concurrent._
import com.colofabrix.scala.figlet4s.core.Figlet4sClient

class EitherFiglet4sSpecs
    extends AnyFlatSpec
    with Matchers
    with Figlet4sMatchers
    with EitherMatchers
    with EitherValues {

  //  Rendering  //

  "Rendering APIs" should "render a default text using the \"standard\" font" in {
    val computed =
      for {
        options <- standardBuilder.options
      } yield {
        val computed = Figlet4s.renderString(standardInput, options)
        val expected = FIGure(options.font, standardInput, Vector(standardLines.toSubcolumns))
        computed should lookLike(expected)
      }

    computed should be(right)
  }

  //  Internal fonts  //

  "Internal Fonts API" should "return the list of internal fonts containing at least the \"standard\" font" in {
    val computed = Figlet4s.internalFonts

    computed should be(right)
    computed.value should contain(Figlet4sClient.defaultFont)
  }

  it should "load all internal fonts successfully" in {
    val fonts = Figlet4s.internalFonts.value
    fonts.map(Figlet4s.loadFontInternal(_).value)
  }

  it should "support loading of internal fonts in parallel" in {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val data = Figlet4s
      .internalFonts
      .value
      .toList
      .take(10)
      .flatMap(Seq.fill(10)(_))

    val test = data.parTraverse { fontName =>
      IO(Figlet4s.loadFontInternal(fontName).value)
    }

    test.unsafeRunSync()
  }

  it should "result in a FigletLoadingError when trying to load a font that doesn't exist" in {
    assertThrows[FigletLoadingError] {
      Figlet4s.loadFontInternal("non_existent").unsafeGet
    }
  }

  //  Fonts  //

  "Fonts API" should "read a font from the file system" in {
    val cwd      = System.getProperty("user.dir")
    val computed = Figlet4s.loadFont(s"$cwd/figlet4s-core/src/main/resources/fonts/standard.flf")
    computed should be(right)
  }

  it should "throw a FigletLoadingError when trying to load a font that doesn't exist" in {
    assertThrows[FigletLoadingError] {
      Figlet4s.loadFont("non_existent").unsafeGet
    }
  }

  //  Builder  //

  "Builder API" should "create a builder" in {
    Figlet4s.builder()
  }

}
