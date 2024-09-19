package com.colofabrix.scala.figlet4s.unsafe

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.core._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.StandardTestData._
import com.colofabrix.scala.figlet4s.testutils._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import scala.concurrent.ExecutionContext
import scala.util._
import scala.util.matching.Regex

class UnsafeFiglet4sSpecs extends AnyFlatSpec with Matchers with Figlet4sMatchers with OriginalFigletTesting {

  //  Rendering  //

  "Rendering APIs" should "render a default text using the \"standard\" font" in {
    val computed = Figlet4s.renderString(standardInput, standardBuilder.options)
    val expected = FIGure(standardBuilder.options.font, standardInput, Vector(standardLines.toSubcolumns))
    computed should lookLike(expected)
  }

  it should "render the texts as the original command line FIGlet does" taggedAs SlowTest in {
    figletRenderingTest { testData =>
      val testBuilder =
        Figlet4s
          .builder()
          .text(testData.renderText)
          .withMaxWidth(testData.renderText.length * 50)
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

  it should "list the correct number of fonts" in {
    val actual   = Figlet4s.internalFonts.length
    val expected = 380 //It's 453 on macOS Sonoma
    actual shouldBe expected
  }

  it should "respect the fonts subdirectories and naming convention" in {
    val actual =
      Figlet4s
        .internalFonts
        .filterNot(startPathRegex.findFirstIn(_).isDefined)

    actual shouldBe empty
  }

  it should "include all fonts subdirectories" in {
    val expected =
      fontsSubdirectories
        .keySet
        .map(subdir => s"$subdir$pathSeparator")
        .toSeq
        .:+("")

    val actual =
      Figlet4s
        .internalFonts
        .groupBy { font =>
          startPathRegex.findFirstMatchIn(font).map(_.group(1))
        }
        .collect {
          case (Some(subdir), _) => subdir
        }

    actual should contain theSameElementsAs expected
  }

  it should "list the correct number of fonts for each subdirectory" in {
    val expected = fontsSubdirectories.values.toSeq

    val actual =
      fontsSubdirectories
        .toSeq
        .map {
          case (subdir, _) =>
            val subdirPathRegex = s"^$subdir\\$pathSeparator.*".r
            Figlet4s
              .internalFonts
              .count(subdirPathRegex.findFirstIn(_).isDefined)
        }

    actual should contain theSameElementsInOrderAs expected
  }

  it should "load all internal fonts successfully" in {
    val loadingErrors =
      for {
        fonts <- Figlet4s.internalFonts
        error <- interpretResult(fonts)(Try(Figlet4s.loadFontInternal(fonts)))
      } yield error

    loadingErrors shouldBe empty
  }

  it should "support loading of internal fonts in parallel" in {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val data =
      Figlet4s
        .internalFonts
        .toList
        .take(20)
        .flatMap(Seq.fill(20)(_))

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

  it should "load a font file" in {
    val fontPath = getClass.getResource("/raw.flf").getPath
    Figlet4s.loadFont(fontPath)
  }

  it should "load a zipped font file" in {
    val fontPath = getClass.getResource("/compressed.flf").getPath
    Figlet4s.loadFont(fontPath)
  }

  it should "use the first added file in a zipped font file" in {
    val fontPath = getClass.getResource("/multiple.flf").getPath
    val font     = Figlet4s.loadFont(fontPath)
    font.comment should include("Standard by Glenn Chappell & Ian Chai")
  }

  it should "error on an empty zipped font file" in {
    val fontPath = getClass.getResource("/empty.flf").getPath
    val caught =
      intercept[FigletLoadingError] {
        Figlet4s.loadFont(fontPath)
      }
    caught.getMessage shouldBe "Cannot read font file from ZIP"
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

  //  Builder  //

  "Builder API" should "create a builder" in {
    Figlet4s.builder()
  }

  //  Support  //

  private lazy val (startPathRegex: Regex, pathSeparator: String) =
    LibraryLocation.discover[cats.Id] match {
      case LibraryLocation.Jar(_) =>
        // NOTE: I do not know how I can ever test this section if not by publishing locally
        val regex     = "^([^/]+/|)[^/]+$".r
        val separator = "/"
        (regex, separator)
      case LibraryLocation.FileSystem(_, separator) =>
        val regex = ("^([^\\" + separator + "]+\\" + separator + "|)[^\\" + separator + "]+$").r
        (regex, separator)
      case LibraryLocation.Unknown =>
        fail("Could not detect the correct location of the current code")
    }

  private lazy val fontsSubdirectories: Map[String, Int] =
    Map(
      //"bdffonts" -> 73, //This is required on macOS Sonoma
      "c64" -> 186,
    )

  private def interpretResult(font: String): PartialFunction[Try[_], Option[String]] = {
    case Failure(fe @ FigletException(message)) =>
      Some(s"${fe.getClass.getSimpleName} on $font: $message")
    case Failure(exception: Throwable) =>
      Some(s"Exception on $font: ${exception.getMessage}")
    case Success(_) =>
      None
  }

}
