package com.colofabrix.scala.figlet4s

import cats.implicits._
import com.colofabrix.scala.figlet4s.options._
import java.io.File
import java.nio.file.Paths
import java.util.regex.Pattern
import org.scalatest._
import sys.process._

/**
 * Support for testing using the figlet executable
 */
trait OriginalFigletTesting {

  def runFiglet(options: RenderOptions, text: String): LazyList[String] = {
    figletCommand(options, text).lazyLines
  }

  def assumeExecutableInPath(executable: String): Unit =
    if (!executableExists(executable))
      Assertions.cancel(s"Executable $executable doesn't exist. Install $executable to run this test.")

  //  Support  //

  private def executableExists(exec: String): Boolean = {
    System
      .getenv("PATH")
      .split(Pattern.quote(File.pathSeparator))
      .map(path => new File(Paths.get(path, exec).toAbsolutePath().toString()))
      .exists(file => file.exists() && file.canExecute())
  }

  private def figletCommand(options: RenderOptions, text: String): String = {
    val maxWidth       = figletWidth(options, text)
    val fontFile       = figletFont(options)
    val hLayout        = figletHorizontalLayout(options)
    val printDirection = figletPrintDirection(options)
    val justification  = figletJustfication(options)

    s"figlet $fontFile $maxWidth $hLayout $printDirection $justification '$text'"
  }

  private def figletWidth(options: RenderOptions, text: String): String =
    s"-w ${options.font.header.maxLength * text.length()}"

  private def figletFont(options: RenderOptions): String = {
    val fontFile =
      Paths.get(
        options.getClass.getProtectionDomain.getCodeSource.getLocation.getPath,
        "fonts",
        options.font.name + ".flf",
      )
    s"-f '$fontFile'"
  }

  private def figletHorizontalLayout(options: RenderOptions): String =
    options.horizontalLayout match {
      case HorizontalLayout.FullWidth               => "-W"
      case HorizontalLayout.HorizontalFitting       => "-k"
      case HorizontalLayout.HorizontalSmushing      => "-s"
      case HorizontalLayout.ForceHorizontalSmushing => "-S"
      case HorizontalLayout.FontDefault             => "-s"
    }

  private def figletPrintDirection(options: RenderOptions): String =
    options.printDirection match {
      case PrintDirection.LeftToRight => "-L"
      case PrintDirection.RightToLeft => "-R"
      case PrintDirection.FontDefault => "-X"
    }

  private def figletJustfication(options: RenderOptions): String =
    options.justification match {
      case Justification.Center      => "-c"
      case Justification.FlushLeft   => "-l"
      case Justification.FlushRight  => "-r"
      case Justification.FontDefault => "-x"
    }
}
