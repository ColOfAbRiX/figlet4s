package com.colofabrix.scala.figlet4s.testutils

import cats.implicits._
import com.colofabrix.scala.figlet4s.compat._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.matchers._

/**
 * ScalaTest matchers for Figlet4s
 */
trait Figlet4sMatchers {

  class FIGureMatchers(expected: FIGure) extends Matcher[FIGure] {
    def apply(computed: FIGure): MatchResult =
      compareFigures(computed, expected) match {
        case Right(_)    => MatchResult(true, "", s"The computed FIGure looks like the expected FIGure.")
        case Left(error) => MatchResult(false, error, s"The computed FIGure looks like the expected FIGure.")
      }

    //  Support  //

    private def compareFigures(computed: FIGure, expected: FIGure): Either[String, Unit] =
      for {
        _ <- compareTexts(computed, expected)
        _ <- compareFonts(computed, expected)
        _ <- compareColumns(computed, expected)
      } yield ()

    private def compareTexts(computed: FIGure, expected: FIGure): Either[String, Unit] =
      Either.cond(
        computed.value === expected.value,
        (),
        s"The text '${computed.value}' of the computed FIGure doesn't match the text '${expected.value}' of the " +
        s"expected FIGure",
      )

    private def compareFonts(computed: FIGure, expected: FIGure): Either[String, Unit] =
      Either.cond(
        computed.font === expected.font,
        (),
        s"The font '${computed.font.name}' of the computed FIGure doesn't match the font '${expected.font.name}' of " +
        s"the expected FIGure",
      )

    private def compareColumns(computed: FIGure, expected: FIGure): Either[String, Unit] = {
      val cc = computed.cleanColumns.headOption.getOrElse(SubColumns(Vector.empty))
      val ec = expected.cleanColumns.headOption.getOrElse(SubColumns(Vector.empty))

      // Discover max length for column padding
      val maxC    = cc.value.map(_.length).maxOption.getOrElse(0)
      val maxE    = ec.value.map(_.length).maxOption.getOrElse(0)
      val max     = Math.max(maxC, maxE)
      val margins = " +" + "|" * max + "+ "

      lazy val differences =
        for {
          ((c, e), i) <- cc.value.zip(ec.value).zipWithIndex
          result      <- columnsDiff(i, c, e, max)
        } yield {
          result
        }

      lazy val paddedDiffs = {
        val maxLength = differences.map(_.length).max
        differences.map(x => x + " " * (maxLength - x.length))
      }

      lazy val printableDiffs =
        SubColumns(Vector(margins) ++: paddedDiffs ++: Vector(margins))
          .toSublines
          .map { x =>
            if (x.length > consoleWidth - 13)
              x.substring(0, consoleWidth - 13) + "..."
            else x
          }
          .toString

      lazy val diffMessage =
        s"The expected FIGure doesn't look like the computed FIGure. Here is a breakdown of the differences:\n\n" +
        s"Text: '${computed.value}'\n\n" +
        s"Font: \n${niceFont(computed.font)}\n\n" +
        s"Expected:\n${expected.asString()}\n\n" +
        s"Computed:\n${computed.asString()}\n\n" +
        s"Differences:\n$printableDiffs"

      Either.cond(computed.cleanColumns === expected.cleanColumns, (), diffMessage)
    }

    private def niceFont(font: FIGfont): String = {
      s"  Name: ${font.name}\n" +
      s"  Hardblank: ${font.header.hardblank}\n" +
      s"  Height: ${font.header.height}\n" +
      s"  Baseline: ${font.header.baseline}\n" +
      s"  Max Length: ${font.header.maxLength}\n" +
      s"  Old Layout: ${font.header.oldLayout}\n" +
      s"  Comment Lines: ${font.header.commentLines}\n" +
      s"  Print Direction: ${font.header.printDirection}\n" +
      s"  Full Layout: ${font.header.fullLayout}\n" +
      s"  Codetag Count: ${font.header.codetagCount}"
    }

    private def columnsDiff(i: Int, e: String, c: String, maxLength: Int): Vector[String] = {
      val paddedC = padToLen(c)(maxLength, " ")
      val paddedE = padToLen(e)(maxLength, " ")

      val result = if (e =!= c) {
        val List(n1, n2, n3) = "%3d".format(i).toList.map(_.toString)
        val spacer           = "+" + "|" * (e.length) + "+ "
        Vector(
          "|" + spacer,
          n1 + s"-$paddedC-E",
          n2 + spacer,
          n3 + s"-$paddedE-C",
          "|" + spacer,
        )
      } else Vector(s" -$paddedE- ")

      result
    }

    private val consoleWidth = Option(System.getenv("COLUMNS")).map(_.toInt).getOrElse(120)
  }

  private def padToLen(self: String)(len: Int, elem: String): String =
    self + elem * Math.max(len - self.length, 0)

  /**
   * Compares a FIGure with another FIGure to see if they look the same
   */
  def lookLike(expected: FIGure): FIGureMatchers =
    new FIGureMatchers(expected)

  ->()
}

object Figlet4sMatchers extends Figlet4sMatchers
