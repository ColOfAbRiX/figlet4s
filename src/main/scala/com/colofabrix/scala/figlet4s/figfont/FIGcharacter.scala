package com.colofabrix.scala.figlet4s.figfont

import cats.implicits._
import com.colofabrix.scala.figlet4s._

case class FIGcharacter private (
    name: Char,
    lines: Vector[String],
    endmark: Char,
    width: Int,
)

object FIGcharacter {
  def apply(name: Char, lines: Vector[String]): FigletResult[FIGcharacter] = {
    // Discover character endmark
    val endmarkV = {
      val allEndmarks = lines.map(_.last).toSet
      Option
        .when(allEndmarks.size == 1)(allEndmarks.headOption)
        .flatten
        .toValidNec(FlfCharacterError(s"Multiple characters endmark found: ${allEndmarks.toString}"))
    }

    endmarkV andThen { endmark =>
      // Remove endmarks from lines
      val cleanLines = lines.map(_.replaceAll(endmark.toString + "+$", ""))

      // Discover character width
      val charWidthV = {
        val allLinesWidth = cleanLines.map(_.length).toSet
        Option
          .when(allLinesWidth.size == 1)(allLinesWidth.headOption)
          .flatten
          .toValidNec(FlfCharacterError(s"Lines for character '$name' are of different width"))
      }

      charWidthV andThen { charWidth =>
        FIGcharacter(name, cleanLines, endmark, charWidth).validNec
      }
    }
  }
}
