package com.colofabrix.scala.figlet4s.figfont

import cats.data.Validated
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import scala.util.matching.Regex

/**
 * The definition of a single FIGlet character, part of a FIGfont, composed of SubLines
 */
final case class FIGcharacter private[figlet4s] (
    fontId: String,
    name: Char,
    lines: SubLines,
    endmark: Char,
    width: Int,
    comment: Option[String],
    position: Int,
) {
  lazy val columns: SubColumns = lines.toSubcolumns

  override def toString(): String =
    lines.value.mkString("\n")
}

object FIGcharacter {
  /**
   * Creates a validated FIGcharacter
   */
  def apply(
      fontId: String,
      maxLength: Int,
      height: Int,
      name: Char,
      lines: SubLines,
      comment: Option[String],
      position: Int,
  ): FigletResult[FIGcharacter] = {
    val maxLengthV = Validated.condNec(
      maxLength > 0,
      maxLength,
      FIGheaderError(s"Value of 'maxLength' must be positive: $maxLength"),
    )
    val argHeightV = Validated.condNec(
      height > 0,
      height,
      FIGheaderError(s"Value of 'height' must be positive: $height"),
    )
    val nameV       = if (name =!= '\uffff') name.validNec else FIGcharacterError(s"Name '-1' is illegal").invalidNec
    val endmarkV    = validateEndmark(name, position, lines)
    val cleanLinesV = endmarkV andThen cleanLines(lines)
    val widthV      = maxLengthV.andThen { cleanLinesV andThen validateWidth(name, _, position) }
    val heightV     = argHeightV.andThen { cleanLinesV andThen validateHeight(name, position, _) }

    heightV andThen { _ =>
      (fontId.validNec, nameV, cleanLinesV, endmarkV, widthV, comment.validNec, position.validNec)
        .mapN(FIGcharacter.apply)
    }
  }

  /**
   * Creates a validated FIGcharacter
   */
  def apply(
      fontId: String,
      header: FIGheader,
      name: Char,
      lines: SubLines,
      comment: Option[String],
      position: Int,
  ): FigletResult[FIGcharacter] =
    apply(fontId, header.maxLength, header.height, name, lines, comment, position)

  /**
   * Validates all lines endmarks
   */
  private def validateEndmark(name: Char, position: Int, lines: SubLines): FigletResult[Char] = {
    val allEndmarks = lines.value.map(_.last).toSet
    allEndmarks
      .headOption
      .filter(_ => allEndmarks.size === 1)
      .toValidNec(
        FIGcharacterError(
          s"Multiple endmarks found for character '$name' defined at line ${position + 1}, " +
          s"only one endmark character is allowed: $allEndmarks",
        ),
      )
  }

  /**
   * Removes the endmarks from the lines of the character
   */
  private def cleanLines(lines: SubLines)(endmark: Char): FigletResult[SubLines] = {
    val find = Regex.quote(endmark.toString) + "{1,2}$"
    lines.map(_.replaceAll(find, "")).validNec
  }

  /**
   * Validates the width of each line
   */
  private def validateWidth(name: Char, maxLength: Int, position: Int)(cleanLines: SubLines): FigletResult[Int] = {
    val allLinesWidth = cleanLines.value.map(_.length).toSet
    if (maxLength <= 0)
      FIGcharacterError(s"The argument 'maxLength' must be greater than zero: $maxLength").invalidNec
    else
      allLinesWidth
        .headOption
        .filter(_ => allLinesWidth.size === 1)
        .toValidNec(
          FIGcharacterError(
            s"Lines for character '$name' defined at line ${position + 1} are of different " +
            s"width: $cleanLines",
          ),
        )
        .andThen { width =>
          if (width <= maxLength) width.validNec
          else FIGcharacterError(s"""Maximum character width exceeded at line ${position + 1}""").invalidNec
        }
  }

  /**
   * Validates the height of each line
   */
  private def validateHeight(name: Char, position: Int, height: Int)(cleanLines: SubLines): FigletResult[Int] =
    if (height <= 0)
      FIGcharacterError(s"The argument 'height' must be greater than zero: $height").invalidNec
    else if (cleanLines.value.size === height)
      height.validNec[FigletError]
    else
      FIGcharacterError(
        s"The character '$name' defined at line ${position + 1} doesn't respect the specified " +
        s"height of $height",
      ).invalidNec
}
