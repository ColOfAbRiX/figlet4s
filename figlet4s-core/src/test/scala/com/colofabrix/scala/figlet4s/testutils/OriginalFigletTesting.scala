package com.colofabrix.scala.figlet4s.testutils

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import java.io.File
import java.nio.file.Paths
import java.util.regex.Pattern
import org.scalacheck._
import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks._
import scala.util._
import sys.process._

/**
 * Support for testing using the figlet executable
 */
trait OriginalFigletTesting {

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
  def figlet4sRenderingTest[A](f: String => A): Unit = {
    assumeExecutableInPath("figlet")
    forAll((figfontCharsGen, "renderText"), minSuccessful(50)) { text =>
      whenever(text.forall(FIGfont.requiredChars.contains(_))) {
        f(text)
      }
    }
  }

  //  Support  //

  private val figfontCharsGen: Gen[String] =
    Gen
      .someOf(FIGfont.requiredChars)
      .suchThat(x => x.length < 100)
      .map(Random.shuffle(_))
      .map(_.mkString)

  // private val figfontGen: Gen[HorizontalLayout] =
  //   Gen.oneOf(List("standard"))

  // private val horizontalLayoutGen: Gen[HorizontalLayout] =
  //   Gen.oneOf(HorizontalLayout.values)

  // private val printDirectionGen: Gen[PrintDirection] =
  //   Gen.oneOf(PrintDirection.values)

  // private val justificationGen: Gen[Justification] =
  //   Gen.oneOf(Justification.values)

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
    List("figlet") ++: fontFile ++: maxWidth ++: hLayout ++: printDirection ++: justification ++: List(text)
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
