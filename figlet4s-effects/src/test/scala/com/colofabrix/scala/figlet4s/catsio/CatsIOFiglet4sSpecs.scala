package com.colofabrix.scala.figlet4s.catsio

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.core.Figlet4sClient
import com.colofabrix.scala.figlet4s.errors._
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import scala.concurrent._

class CatsIOFiglet4sSpecs extends AnyFlatSpec with Matchers with OptionValues {

  private def run[A](a: IO[A]): A = a.unsafeRunSync()

  //  Internal fonts  //

  "Internal Fonts API" should "return the list of internal fonts containing at least the \"standard\" font" in {
    run(Figlet4s.internalFonts) should contain(Figlet4sClient.defaultFont)
  }

  it should "load all internal fonts successfully" in {
    val fonts = run(Figlet4s.internalFonts)
    val test  = fonts.traverse(Figlet4s.loadFontInternal(_))
    run(test)
  }

  it should "support loading of internal fonts in parallel" in {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val fonts = run(Figlet4s.internalFonts)
    val data = fonts
      .toList
      .take(10)
      .flatMap(Seq.fill(10)(_))

    val test = data.parTraverse { fontName =>
      Figlet4s.loadFontInternal(fontName)
    }

    run(test)
  }

  it should "result in a FigletLoadingError when trying to load a font that doesn't exist" in {
    assertThrows[FigletLoadingError] {
      run(Figlet4s.loadFontInternal("non_existent"))
    }
  }

  //  Fonts  //

  "Fonts API" should "read a font from the file system" in {
    val cwd  = System.getProperty("user.dir")
    val test = Figlet4s.loadFont(s"$cwd/figlet4s-core/src/main/resources/fonts/standard.flf")
    run(test)
  }

  it should "throw a FigletLoadingError when trying to load a font that doesn't exist" in {
    assertThrows[FigletLoadingError] {
      run(Figlet4s.loadFont("non_existent"))
    }
  }

  //  Builder  //

  "Builder API" should "create a builder" in {
    Figlet4s.builder()
  }

}
