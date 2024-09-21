package com.colofabrix.scala.figlet4s.catsio

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.core.Figlet4sClient
import com.colofabrix.scala.figlet4s.errors._
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import cats.effect.unsafe.implicits.global

class CatsIOFiglet4sSpecs extends AnyFlatSpec with Matchers with OptionValues {

  private val figlet4s = new Figlet4s()

  private def run[A](a: IO[A]): A = a.unsafeRunSync()

  //  Internal fonts  //

  "Internal Fonts API" should "return the list of internal fonts containing at least the \"standard\" font" in {
    run(figlet4s.internalFonts) should contain(Figlet4sClient.defaultFont)
  }

  it should "load all internal fonts successfully" in {
    val fonts = run(figlet4s.internalFonts)
    val test  = fonts.toList.traverse(figlet4s.loadFontInternal)
    run(test)
  }

  it should "support loading of internal fonts in parallel" in {
    val fonts = run(figlet4s.internalFonts)
    val data = fonts
      .toList
      .take(10)
      .flatMap(Seq.fill(10)(_))

    val test = data.parTraverse { fontName =>
      figlet4s.loadFontInternal(fontName)
    }

    run(test)
  }

  it should "result in a FigletLoadingError when trying to load a font that doesn't exist" in {
    assertThrows[FigletLoadingError] {
      run(figlet4s.loadFontInternal("non_existent"))
    }
  }

  //  Fonts  //

  "Fonts API" should "read a font from the file system" in {
    val cwd  = System.getProperty("user.dir")
    val test = figlet4s.loadFont(s"$cwd/figlet4s-core/src/main/resources/fonts/standard.flf")
    run(test)
  }

  it should "throw a FigletLoadingError when trying to load a font that doesn't exist" in {
    assertThrows[FigletLoadingError] {
      run(figlet4s.loadFont("non_existent"))
    }
  }

  //  Builder  //

  "Builder API" should "create a builder" in {
    figlet4s.builder()
  }

}
