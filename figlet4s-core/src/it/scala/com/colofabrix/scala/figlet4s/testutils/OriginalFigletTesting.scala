package com.colofabrix.scala.figlet4s.testutils

import cats.effect._
import cats.effect.implicits._
import cats.effect.unsafe.implicits.global
import cats.implicits._
import com.colofabrix.scala.figlet4s.compat._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.unsafe._
import java.io.{ File, FileInputStream, FileOutputStream }
import java.nio.file.Paths
import java.util.regex.Pattern
import org.scalacheck._
import org.scalactic.anyvals._
import org.scalatest._
import org.scalatestplus.scalacheck._
import scala.collection.mutable
import scala.collection.concurrent.TrieMap
import scala.sys.process.{ Process, ProcessLogger }
import scala.util.Using

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

  implicit val generatorDrivenConfig: PropertyCheckConfiguration =
    PropertyCheckConfiguration(minSuccessful = PosInt(20))

  //  API  //
  /**
   * Renders a text with the given options using the figlet executable found on command line.
   * Returns None when figlet fails to render usable output: figlet 2.2.5 aborts, produces no
   * output or emits random bytes on a small number of inputs, and in all those cases there is
   * no usable output to compare with.
   */
  def renderWithFiglet(options: RenderOptions, text: String): Option[FIGure] = {
    val command = figletCommand(options, text)
    Iterator
      .range(1, 4)
      .map(_ => runFiglet(command))
      .collectFirst { case Some(output) => toFIGure(options, text, output) }
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
    val parallelism = Runtime.getRuntime.availableProcessors

    val parallelTests = 
      for {
        _              <- Vector(assumeExecutableInPath("figlet"))
        fontName       <- Figlet4s.internalFonts.filterNot(dodgyFonts)
        hLayout        <- HorizontalLayout.values.filterNot(_ == HorizontalLayout.ForceHorizontalSmushing)
        printDirection <- PrintDirection.values
        justification  <- Justification.values
      } yield {
        TestRenderOptions("", fontName, hLayout, printDirection, justification)
      }

    // NOTE: The original figlet 2.2.5 renders right-to-left figures unreliably: it can emit random bytes and
    //       produces kerning and smushing results that differ from Figlet4s, so RTL renders are not compared
    val comparableTests = parallelTests.filterNot(_.printDirection == PrintDirection.RightToLeft)

    note(s"Running ${comparableTests.size} Figlet test cases")

    comparableTests
      .parTraverseN(parallelism * 2)(runTests(f))
      .map(_ => ())
      .unsafeRunSync()
  }

  //  Support  //

  private def runTests[A](f: TestRenderOptions => A)(testData: TestRenderOptions): IO[Unit] =
    IO.blocking {
      val cycleGen    =
        renderTextGen.map { text =>
          testData.copy(renderText = text)
        }

      forAll((cycleGen, "testData"))(f)
    }

  //  Font access is slow on network filesystems: fonts are parsed once and copied once to the local
  //  temporary directory, so both Figlet4s and the original figlet always read from a fast local disk
  private val fontCache  = TrieMap.empty[String, FIGfont]
  private val fontMirror = TrieMap.empty[File, File]

  def cachedFont(fontName: String): FIGfont =
    fontCache.getOrElseUpdate(fontName, Figlet4s.loadFontInternal(fontName))

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

  private def runFiglet(command: List[String]): Option[Vector[String]] = {
    val lines    = mutable.ListBuffer.empty[String]
    val exitCode = Process(command).run(ProcessLogger(lines += _, _ => ())).exitValue()

    if (exitCode == 0 && isUsable(lines.toVector)) 
      Some(lines.toVector) 
    else
      None
  }

  private def isUsable(lines: Vector[String]): Boolean =
    lines.nonEmpty && lines.forall(_.forall(c => c >= 32 && c <= 126))

  private def toFIGure(options: RenderOptions, text: String, output: Vector[String]): FIGure = {
    val maxWidth      = output.map(_.length).maxOption.getOrElse(0)
    val uniformOutput = output.map(line => line + " " * (maxWidth - line.length))
    FIGure(options.font, text, Vector(SubLines(uniformOutput).toSubcolumns))
  }

  // NOTE: Some fonts are known to have issues: https://github.com/pwaller/pyfiglet/blob/master/pyfiglet/test.py#L37
  private def dodgyFonts(fontName: String): Boolean = {
    val dodgyList = List(
      // NOTE: The original figlet renders this font as all whitespaces, maybe the font is corrupted?
      "dosrebel",
      // NOTE: This font uses a sub-character with code greater than 127, which the original figlet treats as
      //       smushable background during smushing. Figlet4s doesn't implement this rule yet and renders it wider
      "contributed/l4me",
      // NOTE: This font's smushing rules overlap multi-byte UTF-8 characters: the original figlet smushes them at
      //       byte level, Figlet4s smushes them at character level and the results differ on the glyph edges
      "xero/tubessmushed",
      // NOTE: The original figlet produces an empty or heavily-offset output for this font: some glyphs render
      //       as nothing at all and the others get a large left margin, while Figlet4s renders them normally
      "xero/ublk",
      // NOTE: The original figlet produces wrong output for this font with several layout and print direction
      //       combinations: glyphs go missing or the output contains random bytes (reads uninitialized memory?)
      "1row",
      // NOTE: The original figlet drops the last column of some glyphs (e.g. '#' loses its rightmost shadow
      //       column), while Figlet4s renders the glyph exactly as stored in the font file
      "henry3d",
    )

    dodgyList.contains(fontName)
  }

  // NOTE: I found issues when rendering higher-number characters with figlet so I decided to work on only a subset
  //       of a font's charset. The ideal scenario would be to test all possible characters: font.characters.keySet
  private def safeCharset: Seq[Char] =
    (32 to 126).map(_.toChar)

  private def renderTextGen: Gen[String] =
    Gen
      .chooseNum(0, 10)
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
