package com.colofabrix.scala.figlet4s.figfont

import cats.implicits._
import com.colofabrix.scala.figlet4s._
import scala.util.matching.Regex

/**
 * A single FIGlet character
 */
final case class FIGcharacter(
    name: Char,
    lines: Vector[String],
    endmark: Char,
    width: Int,
    comment: Option[String],
    position: Int,
)

object FIGcharacter {
  def apply(
      header: FlfHeader,
      name: Char,
      lines: Vector[String],
      comment: Option[String],
      position: Int,
  ): FigletResult[FIGcharacter] = {
    val endmarkV    = validateEndmark(name, position, lines)
    val cleanLinesV = endmarkV andThen cleanLines(lines)
    val widthV      = cleanLinesV andThen validateWidth(name, header.maxLength, position)
    val heightV     = cleanLinesV andThen validateHeight(name, position, header.height)

    heightV andThen { _ =>
      (name.validNec, cleanLinesV, endmarkV, widthV, comment.validNec, position.validNec)
        .mapN(FIGcharacter.apply)
    }
  }

  /**
   * Validates all lines endmarks
   */
  private def validateEndmark(name: Char, position: Int, lines: Vector[String]): FigletResult[Char] = {
    val allEndmarks = lines.map(_.last).toSet
    allEndmarks
      .headOption
      .filter(_ => allEndmarks.size == 1)
      .toValidNec(
        FlfCharacterError(
          s"""|Multiple endmarks found for character '$name' defined at line ${position + 1}, only one character allowed:
              |${allEndmarks.toString}""".stripMargin,
        ),
      )
  }

  /**
   * Removes the endmarks from the lines of the character
   */
  private def cleanLines(lines: Vector[String])(endmark: Char): FigletResult[Vector[String]] = {
    val find = Regex.quote(endmark.toString) + "{1,2}$"
    lines.map(_.replaceAll(find, "")).validNec
  }

  /**
   * Validates the width of each line
   */
  private def validateWidth(name: Char, maxWidth: Int, position: Int)(cleanLines: Vector[String]): FigletResult[Int] = {
    val allLinesWidth = cleanLines.map(_.length).toSet
    allLinesWidth
      .headOption
      .filter(_ => allLinesWidth.size == 1)
      .toValidNec(
        FlfCharacterError(
          s"""Lines for character '$name' defined at line ${position + 1} are of different width: ${cleanLines.toString}""",
        ),
      )
      .andThen { width =>
        if (width <= maxWidth) width.validNec
        else FlfCharacterError(s"""Maximum character width exceeded at line ${position + 1}""").invalidNec
      }
  }

  /**
   * Validates the height of each line
   */
  private def validateHeight(name: Char, position: Int, height: Int)(cleanLines: Vector[String]): FigletResult[Int] =
    if (cleanLines.size == height)
      height.validNec[FigletError]
    else
      FlfCharacterError(
        s"The character '$name' defined at line ${position + 1} doesn't respect the specified height of $height",
      ).invalidNec
}
