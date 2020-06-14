package com.colofabrix.scala.figlet4s.figfont

import cats.data.Nested
import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont.FIGure._

/**
 * A FIGure that is a rendered String with a specific FIGfont and built built from multiple FIGcharacters
 */
final case class FIGure private[figlet4s] (
    font: FIGfont,
    value: String,
    lines: Vector[FIGline],
) {
  /**
   * The width of the FIGure
   */
  val width: Int = lines.head.head.size

  // /**
  //  * Returns a FIGline as a list of columns
  //  */
  // def columns(line: Int): FIGcolumn =
  //   lines(line)
  //     .toVector
  //     .transpose
  //     .map(_.mkString)

  /**
   * Cached access to the last line of the FIGure
   */
  lazy val lastLine: FIGline = lines.last

  // /**
  //  * Cached access to last line of the FIGure as columns
  //  */
  // lazy val lastLineColumns: FIGcolumn =
  //   columns(0)

  /**
   * Lines stripped of their hardblanks
   */
  val cleanLines: Vector[FIGline] =
    Nested(lines)
      .map(_.replace(font.header.hardblank.toString, " "))
      .value
}

object FIGure {
  type FIGline   = Vector[String]
  type FIGcolumn = Vector[String]

  def apply(font: FIGfont): FIGure =
    FIGure(font.zero, font)

  def apply(char: FIGcharacter, font: FIGfont): FIGure =
    FIGure(font, char.name.toString, Vector(char.lines))

  def apply(char: Char, font: FIGfont): FIGure =
    FIGure(font, char.toString, Vector(font(char).lines))
}
