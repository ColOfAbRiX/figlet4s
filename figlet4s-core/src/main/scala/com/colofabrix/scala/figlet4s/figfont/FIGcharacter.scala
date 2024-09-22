package com.colofabrix.scala.figlet4s.figfont

import cats.data.Validated
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._

import scala.util.matching.Regex

/**
 * The definition of a single FIGlet character, part of a FIGfont, composed of SubLines.
 *
 * A FIGcharacter cannot be instantiated directly as a case class but one must go through the factory methods defined
 * in the companion object [[FIGcharacter$ FIGcharacter]] that perform validation of the defining lines of the character
 *
 * @param fontId   The identifier code of the FIGfont where this FIGcharacter belongs
 * @param name     The name of the FIGcharacter, which is the Char that this FIGcharacter represents
 * @param lines    The strings composing the lines of the FIGcharacter
 * @param endmark  The character marking the end of a line in the definition of the FIGcharacter
 * @param width    The width of the FIGcharacter
 * @param comment  The comment of the FIGcharacter present only if it is not part of the required characters
 * @param position The line in the file where the FIGcharacter is defines
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
  /** The strings composing the column of the FIGcharacter */
  lazy val columns: SubColumns = lines.toSubcolumns
}

object FIGcharacter {
  /**
   * Creates a validated FIGcharacter checking for height and width of the given lines
   *
   * @param fontId   The identifier code of the FIGfont where this FIGcharacter belongs
   * @param maxWidth Maximum width that the FIGcharacter can have
   * @param height   The height of the FIGcharacter that must be respected
   * @param name     The name of the FIGcharacter, which is the Char that this FIGcharacter represents
   * @param lines    The strings composing the lines of the FIGcharacter
   * @param comment  The comment of the FIGcharacter present only if it is not part of the required characters
   * @param position The line in the file where the FIGcharacter is defines
   * @return A [[com.colofabrix.scala.figlet4s.errors.FigletResult FigletResult]] containing the new FIGcharacter or a
   *         list of errors occurred during the creation
   */
  def apply(
      fontId: String,
      maxWidth: Int,
      height: Int,
      name: Char,
      lines: SubLines,
      comment: Option[String],
      position: Int,
  ): FigletResult[FIGcharacter] = {
    val commentV: FigletResult[Option[String]] = comment.validNec

    val fontIdV: FigletResult[String] = fontId.validNec

    val nameV: FigletResult[Char] =
      if (name =!= '\uffff') name.validNec
      else FIGcharacterError(s"Name '-1' is illegal").invalidNec

    val endmarkV: FigletResult[Char]        = validateEndmark(name, position, lines)
    val cleanLinesV: FigletResult[SubLines] = endmarkV andThen cleanLines(lines)

    val argHeightV = Validated.condNec(
      height > 0,
      height,
      FIGheaderError(s"Value of 'height' must be positive: $height"),
    )
    val heightV: FigletResult[Int] = argHeightV andThen { cleanLinesV andThen validateHeight(name, position, _) }

    val maxWidthV = Validated.condNec(
      maxWidth > 0,
      maxWidth,
      FIGheaderError(s"Value of 'maxWidth' must be positive: $maxWidth"),
    )
    val widthV: FigletResult[Int] = maxWidthV andThen { cleanLinesV andThen validateWidth(name, _, position) }

    val positionV: FigletResult[Int] = position.validNec

    heightV andThen { _ =>
      (fontIdV, nameV, cleanLinesV, endmarkV, widthV, commentV, positionV).mapN(FIGcharacter.apply)
    }
  }

  /**
   * Creates a validated FIGcharacter using a FIGheader to validate given lines
   *
   * @param fontId   The identifier code of the FIGfont where this FIGcharacter belongs
   * @param header   The FIGheader used to validate the lines
   * @param name     The name of the FIGcharacter, which is the Char that this FIGcharacter represents
   * @param lines    The strings composing the lines of the FIGcharacter
   * @param comment  The comment of the FIGcharacter present only if it is not part of the required characters
   * @param position The line in the file where the FIGcharacter is defines
   * @return A [[com.colofabrix.scala.figlet4s.errors.FigletResult FigletResult]] containing the new FIGcharacter or a
   *         list of errors occurred during the creation
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
    val linesTerminations =
      lines
        .value
        .flatMap {
          """(.)\1?$""".r.findFirstIn(_).toList.headOption.toList
        }
        .map(_.trim())

    val extractedEndmarksV =
      Validated.condNec(
        linesTerminations.forall(l => l.nonEmpty && l.length <= 2),
        linesTerminations.flatMap(_.toList).toSet,
        FIGcharacterError(
          s"Can't determine endmark. There are lines with no termination or more than 2-characters termination on " +
          s"character '$name' defined at line ${position + 1}: ${linesTerminations.mkString("(", ", ", ")")}",
        ),
      )

    val chosenEndmarkV = extractedEndmarksV andThen { extractedEndmarks =>
      Validated.condNec(
        extractedEndmarks.size === 1,
        extractedEndmarks.headOption,
        FIGcharacterError(
          s"Multiple endmarks found for character '$name' defined at line ${position + 1}, " +
          s"only one endmark character is allowed: ${linesTerminations.mkString("(", ", ", ")")}",
        ),
      )
    }

    chosenEndmarkV andThen {
      _.toValidNec(FIGcharacterError(s"Can't determine endmark for character '$name' defined at line ${position + 1}"))
    }
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
      height.validNec[FigletException]
    else
      FIGcharacterError(
        s"The character '$name' defined at line ${position + 1} doesn't respect the specified " +
        s"height of $height",
      ).invalidNec
}
