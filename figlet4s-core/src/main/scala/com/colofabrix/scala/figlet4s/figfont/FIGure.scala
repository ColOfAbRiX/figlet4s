package com.colofabrix.scala.figlet4s.figfont

import com.colofabrix.scala.figlet4s.compat._

/**
 * A FIGure that is a rendered String with a specific FIGfont and built built from multiple FIGcharacters
 *
 * A FIGure cannot be instantiated directly as a case class but one must go through the factory methods defined
 * in the companion object [[FIGure$ FIGure]] that perform validation of the defining lines of the character
 *
 * @param font    The FIGfont used to render the FIGure
 * @param value   The String value rendered in the FIGure
 * @param columns The FIGure represented with a collection of columns
 */
final case class FIGure private[figlet4s] (
    font: FIGfont,
    value: String,
    columns: Seq[SubColumns],
) {
  private val hardblank =
    font.header.hardblank.toString

  /**
   * The FIGure represented with a collection of lines
   */
  lazy val lines: Seq[SubLines] =
    columns.map(_.toSublines)

  /**
   * The FIGure represented with a list of lines stripped of their hardblanks
   */
  lazy val cleanLines: Seq[SubLines] =
    lines.map(_.replace(hardblank, " "))

  /**
   * The columns representing the rendered FIGure stripped of their hardblanks
   */
  lazy val cleanColumns: Seq[SubColumns] =
    columns.map(_.replace(hardblank, " "))

  /**
   * The max width of the FIGure
   */
  val width: Int =
    lines
      .flatMap(_.value.map(_.length))
      .maxOption
      .getOrElse(0)
}

object FIGure {
  /**
   * Creates an empty FIGure (set to [[com.colofabrix.scala.figlet4s.figfont.FIGfont.zero FIGfont.zero]]) that uses the
   * given FIGfont.
   *
   * @param font The FIGfont that the FIGure uses
   * @return An empty FIGure that uses the given FIGfont
   */
  def apply(font: FIGfont): FIGure =
    FIGure(font.zero, font)

  /**
   * Creates a FIGure that contains a single character given as FIGcharacter
   *
   * @param char The first character of the FIGure
   * @param font The FIGfont that the FIGure uses
   * @return An FIGure containing one character that uses the given FIGfont
   */
  def apply(char: FIGcharacter, font: FIGfont): FIGure =
    FIGure(font, char.name.toString, Seq(char.columns))

  /**
   * Creates a FIGure that contains a single character given as a Char
   *
   * @param char The first character of the FIGure
   * @param font The FIGfont that the FIGure uses
   * @return An FIGure containing one character that uses the given FIGfont
   */
  def apply(char: Char, font: FIGfont): FIGure =
    FIGure(font, char.toString, Seq(font(char).columns))

  ->()
}
