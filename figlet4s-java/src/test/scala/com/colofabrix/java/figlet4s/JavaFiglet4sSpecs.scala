package com.colofabrix.java.figlet4s

import com.colofabrix.scala.figlet4s.errors._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import scala.jdk.CollectionConverters
import scala.util._

class JavaFiglet4sSpecs extends AnyFlatSpec with Matchers {

  //  Internal fonts  //

  "Internal Fonts API" should "return the list of internal fonts containing at least the \"standard\" font" in {
    Figlet4s.internalFonts should contain("standard")
  }

  it should "load all internal fonts successfully" in {
    val loadingErrors = for {
      fonts <- CollectionConverters.ListHasAsScala(Figlet4s.internalFonts).asScala
      error <- interpretResult(fonts)(Try(Figlet4s.loadFontInternal(fonts)))
    } yield error
    loadingErrors shouldBe empty
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
    val result = interpretResult("standard")(Try(font))
    result shouldBe empty
  }

  it should "throw a FigletLoadingError when trying to load a font that doesn't exist" in {
    assertThrows[FigletLoadingError] {
      Figlet4s.loadFont("non_existent")
    }
  }

  //  Builder  //

  "Builder API" should "create a builder" in {
    Figlet4s.builder()
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
