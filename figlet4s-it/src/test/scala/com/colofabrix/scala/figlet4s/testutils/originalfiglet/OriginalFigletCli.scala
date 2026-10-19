package com.colofabrix.scala.figlet4s.testutils.originalfiglet

import cats.effect._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import java.io._
import java.nio.file.Paths
import java.util.regex.Pattern
import scala.collection.mutable
import scala.sys.process.{ Process, ProcessLogger }

/** Executes the original figlet executable and converts its output into a FIGure */
object OriginalFigletCli {

  /** Renders a text with the given options using the figlet executable found on command line */
  def render(options: RenderOptions, text: String): IO[FIGure] =
    IO.blocking {
      val command = figletCommand(options, text)
      val output  = runFiglet(command)
      toFIGure(options, text, figletLinesOrCrash(options, text, output))
    }

  /** Checks if an executable is present in the path */
  def executableExists(exec: String): Boolean =
    System
      .getenv("PATH")
      .split(Pattern.quote(File.pathSeparator))
      .map(path => new File(Paths.get(path, exec).toAbsolutePath.toString))
      .exists(file => file.exists() && file.canExecute)

  //  Support  //

  final private case class FigletOutput(exit: Int, stdout: Vector[String], stderr: String)

  private def runFiglet(command: List[String]): FigletOutput = {
    val out  = mutable.ListBuffer.empty[String]
    val err  = mutable.ListBuffer.empty[String]
    val exit = Process(command).run(ProcessLogger(out += _, err += _)).exitValue()
    FigletOutput(exit, out.toVector, err.mkString("\n"))
  }

  private def figletLinesOrCrash(
      options: RenderOptions,
      text: String,
      output: FigletOutput,
  ): Vector[String] = {
    val reason =
      if (output.exit != 0)
        Some(s"non-zero exit code ${output.exit}")
      else if (output.stdout.exists(_.exists(c => c < ' ' || c > '~')))
        Some("control bytes or non-ASCII bytes in output")
      else
        None

    reason.fold(output.stdout) { r =>
      throw new IllegalArgumentException(
        s"Unusable figlet output ($r) for font '${options.font.name}', layout ${options.horizontalLayout}, direction ${options.printDirection}, justification ${options.justification}, text '$text'",
      )
    }
  }

  private def toFIGure(options: RenderOptions, text: String, output: Vector[String]): FIGure = {
    val maxWidth      = output.map(_.length).maxOption.getOrElse(0)
    val uniformOutput = output.map(line => line + " " * (maxWidth - line.length))
    FIGure(options.font, text, Vector(SubLines(uniformOutput).toSubcolumns))
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

  private def figletFont(options: RenderOptions): List[String] =
    List("-f", OriginalFigletFonts.mirroredFile(options.font).getAbsolutePath)

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
