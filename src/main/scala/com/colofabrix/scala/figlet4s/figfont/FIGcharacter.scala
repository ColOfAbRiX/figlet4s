package com.colofabrix.scala.figlet4s.figfont

import cats.implicits._
import com.colofabrix.scala.figlet4s._

/**
 * A single FIGlet character
 */
case class FIGcharacter(
    name: Char,
    lines: Vector[String],
    endmark: Char,
    width: Int,
    height: Int,
    comment: String,
)

object FIGcharacter {
  def apply(name: Char, lines: Vector[String], height: Int, comment: String): FigletResult[FIGcharacter] = {
    val endmarkV = {
      val allEndmarks = lines.map(_.last).toSet
      Option
        .when(allEndmarks.size == 1)(allEndmarks.headOption)
        .flatten
        .toValidNec(FlfCharacterError(s"Multiple endmarks found, only one character allowed: ${allEndmarks.toString}"))
    }

    val cleanLinesV = endmarkV andThen { endmark =>
        lines.map(_.replaceAll(endmark.toString + "+$", "")).validNec
      }

    val widthV = cleanLinesV andThen { cleanLines =>
        val allLinesWidth = cleanLines.map(_.length).toSet
        Option
          .when(allLinesWidth.size == 1)(allLinesWidth.headOption)
          .flatten
          .toValidNec(FlfCharacterError(s"Lines for character '$name' are of different width"))
      }

    val heightV = cleanLinesV andThen { cleanLines =>
        if (cleanLines.size == height) height.validNec[FigletError]
        else FlfCharacterError(s"The character doesn't respect the specified height of ${height.toString}").invalidNec
      }

    (name.validNec, cleanLinesV, endmarkV, widthV, heightV, comment.validNec)
      .mapN(FIGcharacter.apply)
  }
}
