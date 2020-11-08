package com.colofabrix.scala.figlet4s.testutils

import cats.effect._
import cats.effect.implicits._
import cats.implicits._
import com.colofabrix.scala.figlet4s.compat._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.unsafe._
import java.io.File
import java.nio.file.Paths
import java.util.regex.Pattern
import org.scalacheck._
import org.scalactic.anyvals._
import org.scalatest._
import org.scalatestplus.scalacheck._
import scala.concurrent.ExecutionContext

/**
 * Support for testing using the command line, original figlet executable
 */
trait OriginalFigletTesting extends Notifying {
  import ScalaCheckDrivenPropertyChecks._

  case class TestRenderOptions(
      renderText: String,
      fontName: String,
      horizontalLayout: HorizontalLayout,
      printDirection: PrintDirection,
      justification: Justification,
  )

  //  Implicits  //

  implicit private val cs: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  implicit val generatorDrivenConfig: PropertyCheckConfiguration =
    PropertyCheckConfiguration(minSuccessful = PosInt(50))

  //  API  //

  /**
   * Renders a text with the given options using the figlet executable found on command line
   */
  def renderWithFiglet(options: RenderOptions, text: String): FIGure = {
    val output        = figletCommand(options, text).runStream.toVector
    val maxWidth      = output.map(_.length()).maxOption.getOrElse(0)
    val uniformOutput = output.map(x => x + " " * (maxWidth - x.length()))
    FIGure(options.font, text, Vector(SubLines(uniformOutput).toSubcolumns))
  }

  /**
   * Checks if an executable is present in the path and cancels the execution of the test if not present
   */
  def assumeExecutableInPath(executable: String): Unit =
    if (!executableExists(executable))
      Assertions.cancel(s"Executable $executable doesn't exist. Install $executable to run this test.")

  /**
   * Runs property testing on a given function to test Figlet4s
   */
  def figletRenderingTest[A](f: TestRenderOptions => A): Unit = {
    val parallelism = Runtime.getRuntime.availableProcessors.toLong

    val parallelTests = for {
      _              <- Vector(assumeExecutableInPath("figlet"))
      fontName       <- Figlet4s.internalFonts.filterNot(dodgyFonts)
      hLayout        <- HorizontalLayout.values.filterNot(_ == HorizontalLayout.ForceHorizontalSmushing)
      printDirection <- Vector(PrintDirection.LeftToRight)
      justification  <- Vector(Justification.FlushLeft)
    } yield {
      TestRenderOptions("", fontName, hLayout, printDirection, justification)
    }

    parallelTests
      .parTraverseN(parallelism)(runTests(f))
      .map(_ => ())
      .unsafeRunSync()
  }

  //  Support  //

  private def runTests[A](f: TestRenderOptions => A)(testData: TestRenderOptions): IO[Unit] =
    IO {
      val cycleGen    = renderTextGen.map(text => testData.copy(renderText = text))
      val testDataSet = (cycleGen, "testData")
      forAll(testDataSet)(f)
    }

  // NOTE: Some fonts are known to have issues: https://github.com/pwaller/pyfiglet/blob/master/pyfiglet/test.py#L37
  private def dodgyFonts(fontName: String): Boolean = {
    val dodgyList = List(
      // NOTE: Some fonts seem to treat the hardblanks differently in that they smush together a hardblank and a char
      //       but I can't find what's wrong in Figlet4s implementation of the algorithm (and where to find this
      //       behaviour in the reference doc)
      "alligator",
      "alligator2",
      "alligator3",
      "colossal",
      "univers",
      // NOTE: The original figlet renders this font as all whitespaces, maybe the font is corrupted?
      "dosrebel",
      // NOTE: Doesn't respect spacing (Try "j  k")
      "cricket",
    )

    dodgyList.contains(fontName)
  }

  // NOTE: I found issues when rendering higher-number characters with figlet so I decided to work on only a subset
  //       of a font's charset. The ideal scenario would be to test all possible characters: font.characters.keySet
  private def safeCharset: Seq[Char] =
    (32 to 126).map(_.toChar)

  private def renderTextGen: Gen[String] =
    Gen.listOf[Char](Gen.oneOf(safeCharset)).map(_.mkString)

  private def executableExists(exec: String): Boolean =
    System
      .getenv("PATH")
      .split(Pattern.quote(File.pathSeparator))
      .map(path => new File(Paths.get(path, exec).toAbsolutePath.toString))
      .exists(file => file.exists() && file.canExecute)

  private def figletCommand(options: RenderOptions, text: String): List[String] = {
    val maxWidth       = figletWidth(options, text)
    val fontFile       = figletFont(options)
    val hLayout        = figletHorizontalLayout(options)
    val printDirection = figletPrintDirection(options)
    val justification  = figletJustfication(options)
    List("figlet") ++: fontFile ++: maxWidth ++: hLayout ++: printDirection ++: justification ++: List("--", text)
  }

  private def figletWidth(options: RenderOptions, text: String): List[String] =
    List("-w", (options.font.header.maxLength * text.length()).toString)

  private def figletFont(options: RenderOptions): List[String] = {
    val fontFile =
      Paths.get(
        options.getClass.getProtectionDomain.getCodeSource.getLocation.getPath,
        "fonts",
        options.font.name + ".flf",
      )
    List("-f", fontFile.toAbsolutePath.toString)
  }

  private def figletHorizontalLayout(options: RenderOptions): List[String] =
    options.horizontalLayout match {
      case HorizontalLayout.FullWidth               => List("-W")
      case HorizontalLayout.HorizontalFitting       => List("-k")
      case HorizontalLayout.HorizontalSmushing      => List("-s")
      case HorizontalLayout.ForceHorizontalSmushing => List("-S")
      case HorizontalLayout.FontDefault             => List("-s")
    }

  private def figletPrintDirection(options: RenderOptions): List[String] =
    options.printDirection match {
      case PrintDirection.LeftToRight => List("-L")
      case PrintDirection.RightToLeft => List("-R")
      case PrintDirection.FontDefault => List("-X")
    }

  private def figletJustfication(options: RenderOptions): List[String] =
    options.justification match {
      case Justification.Center      => List("-c")
      case Justification.FlushLeft   => List("-l")
      case Justification.FlushRight  => List("-r")
      case Justification.FontDefault => List("-x")
    }

}
