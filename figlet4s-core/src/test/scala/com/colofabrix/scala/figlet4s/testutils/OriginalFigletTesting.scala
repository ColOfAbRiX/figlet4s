package com.colofabrix.scala.figlet4s.testutils

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import java.io.File
import java.nio.file.Paths
import java.util.regex.Pattern
import org.scalacheck._
import org.scalactic.anyvals._
import org.scalatest._
import org.scalatestplus.scalacheck._
import scala.util._
import sys.process._

/**
 * Support for testing using the command line, original figlet executable
 */
trait OriginalFigletTesting {
  import ScalaCheckDrivenPropertyChecks._

  case class TestRenderOptions(
      renderText: String,
      fontName: String,
      horizontalLayout: HorizontalLayout,
      printDirection: PrintDirection,
      justification: Justification,
  )

  /**
   * Renders a text with the given options using the figlet executable found on command line
   */
  def renderWithFiglet(options: RenderOptions, text: String): FIGure = {
    val output = figletCommand(options, text).lazyLines.toVector
    FIGure(options.font, text, Vector(SubLines(output).toSubcolumns))
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
  def figletRenderingTest[A](f: TestRenderOptions => A): Unit =
    for {
      _              <- Vector(assumeExecutableInPath("figlet"))
      fontName       <- Vector("standard")
      hLayout        <- HorizontalLayout.values
      printDirection <- Vector(PrintDirection.LeftToRight)
      justification  <- Vector(Justification.FlushLeft)
    } {
      runTests(TestRenderOptions("", fontName, hLayout, printDirection, justification))(f)
    }

  //  Support  //

  private def runTests[A](testData: TestRenderOptions)(f: TestRenderOptions => A): Unit = {
    val min         = PosInt.fromOrElse(safeCharset.length, 94)
    val cycleGen    = renderTextGen.map(text => testData.copy(renderText = text))
    val testDataSet = (cycleGen, "testData")

    forAll(testDataSet, minSuccessful(min)) { testData =>
      whenever(testData.renderText.length >= 0 && testData.renderText.forall(safeCharset.contains)) {
        f(testData)
      }
    }
  }

  // NOTE: I found issues when rendering higher-number characters with figlet so I decided to work on only a subset
  //       of a font's charset. The best options would be to test all possible characters: font.characters.keySet
  private def safeCharset: Seq[Char] =
    (32 to 126).filter(x => x =!= '\\' && x =!= ']').map(_.toChar)

  private def renderTextGen: Gen[String] =
    Gen
      .someOf(safeCharset)
      .suchThat(x => x.length <= 50)
      .map(Random.shuffle(_))
      .map(_.mkString)

  private def executableExists(exec: String): Boolean = {
    System
      .getenv("PATH")
      .split(Pattern.quote(File.pathSeparator))
      .map(path => new File(Paths.get(path, exec).toAbsolutePath().toString()))
      .exists(file => file.exists() && file.canExecute())
  }

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
    List("-f", fontFile.toAbsolutePath().toString())
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
