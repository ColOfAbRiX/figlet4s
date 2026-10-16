package com.colofabrix.scala.figlet4s.testutils

import cats.effect._
import cats.effect.implicits.concurrentParTraverseOps
import cats.effect.unsafe.implicits.global
import cats.implicits._
import com.colofabrix.scala.figlet4s.compat._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.unsafe._
import java.io._
import java.nio.file.Paths
import java.util.regex.Pattern
import org.scalacheck._
import org.scalacheck.effect.PropF
import org.scalatest._
import org.scalatest.exceptions.TestFailedException
import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import scala.sys.process.{ Process, ProcessLogger }
import scala.util.Using

/**
 * Support for testing using the command line, original figlet executable.
 */
trait OriginalFigletTesting extends Notifying {

  case class TestRenderOptions(
      renderText: String,
      fontName: String,
      horizontalLayout: HorizontalLayout,
      printDirection: PrintDirection,
      justification: Justification,
  )

  private val MinSuccessfulTests =
    20

  private val Parallelism =
    Runtime.getRuntime.availableProcessors

  //  Font access is slow on network filesystems: fonts are parsed once and copied once to the local
  //  temporary directory, so both Figlet4s and the original figlet always read from a fast local disk
  private val fontCache =
    TrieMap.empty[String, FIGfont]

  private val fontMirror =
    TrieMap.empty[File, File]

  //  API  //

  /**
   * Renders a text with the given options using the figlet executable found on command line.
   */
  def renderWithFiglet(options: RenderOptions, text: String): IO[FIGure] =
    IO.blocking {
      val command = figletCommand(options, text)
      val output  = runFiglet(command)
      toFIGure(options, text, figletLinesOrCrash(options, text, output))
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
  def figletRenderingTest(f: TestRenderOptions => IO[Unit]): Unit = {
    val parallelTests =
      for {
        _              <- Vector(assumeExecutableInPath("figlet"))
        fontName       <- Figlet4s.internalFonts
        hLayout        <- HorizontalLayout.values.filterNot(_ == HorizontalLayout.ForceHorizontalSmushing)
        printDirection <- PrintDirection.values
        justification  <- Justification.values
      } yield {
        TestRenderOptions("", fontName, hLayout, printDirection, justification)
      }

    note(s"Figlet test cases: ${parallelTests.size}")

    parallelTests
      .filterNot(isDisabledInput)
      .parTraverseN(Parallelism)(runProperty(f))
      .map(_ => ())
      .unsafeRunSync()
  }

  def cachedFont(fontName: String): FIGfont =
    fontCache.getOrElseUpdate(fontName, Figlet4s.loadFontInternal(fontName))

  //  Support  //

  private def runProperty(f: TestRenderOptions => IO[Unit])(testData: TestRenderOptions): IO[Unit] = {
    val generator =
      renderTextGen.map { text =>
        testData.copy(renderText = text)
      }

    PropF
      .forAllF(generator) { (sample: TestRenderOptions) =>
        f(sample)
      }
      .check {
        Test.Parameters.default.withMinSuccessfulTests(MinSuccessfulTests)
      }
      .flatMap(checkResult)
  }

  private def checkResult(result: Test.Result): IO[Unit] =
    result.status match {
      case Test.Passed | Test.Proved =>
        IO.unit
      case Test.PropException(_, e, _) =>
        IO.raiseError(e)
      case Test.Failed(args, labels) =>
        val niceArgs = args.map(_.arg).mkString(", ")
        IO.raiseError(new AssertionError(s"Property falsified for [$niceArgs] $labels"))
      case other =>
        IO.raiseError(new AssertionError(s"Property was not satisfied: $other"))
    }

  private case class FigletOutput(exit: Int, stdout: Vector[String], stderr: String)

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
      throw new TestFailedException(
        s"Unusable figlet output ($r) for font '${options.font.name}', layout ${options.horizontalLayout}, direction ${options.printDirection}, justification ${options.justification}, text '$text'",
        0,
      )
    }
  }

  private def toFIGure(options: RenderOptions, text: String, output: Vector[String]): FIGure = {
    val maxWidth      = output.map(_.length).maxOption.getOrElse(0)
    val uniformOutput = output.map(line => line + " " * (maxWidth - line.length))
    FIGure(options.font, text, Vector(SubLines(uniformOutput).toSubcolumns))
  }

  //  Known defects of the original figlet 2.2.5, used to tune the test inputs.
  //  Only specific fonts and input combinations with an observed defect are disabled: see the notes.

  private def isDisabledInput(options: TestRenderOptions): Boolean = {
    val fontDisabled =
      dodgyFonts(options.fontName)

    val inputDisabled =
      disabledInputs.contains {
        (
          options.fontName,
          options.horizontalLayout.toString,
          options.printDirection.toString,
          options.justification.toString,
        )
      }

    val divergentDisabled =
      options.fontName == "c64/e__fist_" &&
      options.printDirection == PrintDirection.RightToLeft

    fontDisabled || inputDisabled || divergentDisabled
  }

  // NOTE: Fonts disabled for every combination because the original figlet cannot render them at all
  private def dodgyFonts(fontName: String): Boolean = {
    // FIXME: Store this list in a tsv file, like `disabledInputs`
    val dodgyList =
      List(
        // NOTE: These fonts use multi-byte UTF-8 sub-characters: the original figlet emits control bytes for
        //       them in every layout and direction combination
        "xero/ansiregular",
        "xero/ansishadow",
        "xero/bloody",
        "xero/calvins",
        "xero/deltacorpspriest1",
        "xero/electronic",
        "xero/elite",
        "xero/koholint",
        "xero/kompaktblk",
        "xero/mono9",
        "xero/sixfo",
        "xero/strongerthanall",
        "xero/theedge",
        "xero/this",
        "xero/tubesregular",
        "xero/tubessmushed",
        "xero/ublk",
        // NOTE: The original figlet renders these fonts as blank output, or control-byte garbage with -R and
        //       kerning layouts
        "dosrebel",
        "obanner/Obanner1",
        // NOTE: This font uses a sub-character with code greater than 127, which the original figlet treats as
        //       smushable background during smushing. Figlet4s doesn't implement this rule yet and renders it wider
        "contributed/l4me",
      )

    dodgyList.contains(fontName)
  }

  // NOTE: Catalogue of the specific (font, layout, direction, justification) combinations for which the original
  //       figlet 2.2.5 aborts or emits nondeterministic garbage
  private lazy val disabledInputs: Set[(String, String, String, String)] = {
    val source = scala.io.Source.fromResource("known-figlet-issues/broken-inputs.tsv")
    try {
      source
        .getLines()
        .filterNot(_.startsWith("#"))
        .filter(_.trim.nonEmpty)
        .map(_.split('|'))
        .map(fields => (fields(0), fields(1), fields(2), fields(3)))
        .toSet
    } finally {
      source.close()
    }
  }

  private def mirroredFontFile(font: FIGfont): File =
    fontMirror.getOrElseUpdate(
      font.file, {
        val mirror = File.createTempFile("figlet4s-font-", ".flf")
        Using.Manager { use =>
          use(new FileInputStream(font.file)).transferTo(use(new FileOutputStream(mirror)))
        }
        mirror
      },
    )

  // NOTE: I found issues when rendering higher-number characters with figlet so I decided to work on only a subset
  //       of a font's charset. The ideal scenario would be to test all possible characters: font.characters.keySet
  private def safeCharset: Seq[Char] =
    (32 to 126).map(_.toChar)

  // NOTE: figlet renders an empty input as an empty output, so the generator only produces non-empty texts
  private def renderTextGen: Gen[String] =
    Gen
      .chooseNum(1, 10)
      .flatMap(length => Gen.listOfN(length, Gen.oneOf(safeCharset)))
      .map(_.mkString)

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

  private def figletFont(options: RenderOptions): List[String] =
    List("-f", mirroredFontFile(options.font).getAbsolutePath)

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
