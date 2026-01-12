package com.colofabrix.scala.figlet4s.either

import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.figlet4s.core.Figlet4sClient
import com.colofabrix.scala.figlet4s.errors._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class EitherFiglet4sSpecs extends AnyFlatSpec with Matchers with EitherMatchers with EitherValues {

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
      unsafeGet(Figlet4s.loadFontInternal("non_existent"))
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
      unsafeGet(Figlet4s.loadFont("non_existent"))
    }
  }

  //  Builder  //

  "Builder API" should "create a builder" in {
    Figlet4s.builder()
  }

}
