package com.colofabrix.scala.figlet4s

import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalacheck._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import org.scalatestplus.scalacheck._
import scala.util._
import sys.process._

class Figlet4sUnsafeSpecs extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks with Matchers {

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
    def interpretResult(font: String): PartialFunction[Try[_], Option[String]] = {
      case Failure(fe @ FigletError(message)) =>
        Some(s"${fe.getClass().getSimpleName()} on $font: $message")
      case Failure(exception: Throwable) =>
        Some(s"Exception on $font: ${exception.getMessage}")
      case Success(_) =>
        None
    }

    val result = for {
      font  <- Figlet4s.internalFonts
      error <- interpretResult(font)(Try(Figlet4s.loadFontInternal(font)))
    } yield error

    result shouldBe empty
  }

  private val figfontCharsGen: Gen[String] = Gen
    .someOf(FIGfont.requiredChars)
    .map(x => Random.shuffle(x).mkString)
    .suchThat(x => x.length < 100 && x.length > 0)
    .suchThat(x => x.length <= 5 || x.contains(' '))
    .map(_.mkString)

  it should "render the texts as the original command line FIGlet does" in {
    assumeExecutableInPath("figlet")

    val builder = Figlet4s.builder().withInternalFont("standard")

    forAll(figfontCharsGen) { text =>
      val maxWidth = builder.options.font.header.maxLength * text.length()

      println(s"Max width: $maxWidth")
      println(s"Text: $text#")
      println(s"Text: ${text.map(_.toString + ".").toList}#")

      val computed = builder.text(text).render().asVector()
      val expected = s"figlet -w $maxWidth '${text}'".lazyLines

      println(s"Expected: \n${expected.mkString("\n")}")

      for ((computedLine, expectedLine) <- (computed zip expected)) {
        computedLine should equal(expectedLine)
      }
    }
  }

  private def executableExists(exec: String): Boolean = {
    import java.util.regex.Pattern
    import java.io.File
    import java.nio.file.Paths
    System
      .getenv("PATH")
      .split(Pattern.quote(File.pathSeparator))
      .map(path => new File(Paths.get(path, exec).toAbsolutePath().toString()))
      .exists(file => file.exists() && file.canExecute())
  }

  private def assumeExecutableInPath(executable: String): Unit =
    if (!executableExists(executable))
      cancel(s"Executable $executable doesn't exist. Install $executable to run this test.")

}
