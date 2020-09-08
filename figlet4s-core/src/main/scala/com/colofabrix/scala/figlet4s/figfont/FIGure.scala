package com.colofabrix.scala.figlet4s.figfont

/**
 * A FIGure that is a rendered String with a specific FIGfont and built built from multiple FIGcharacters
 */
final case class FIGure private[figlet4s] (
    font: FIGfont,
    value: String,
    columns: Seq[SubColumns],
) {
  private val hardblank =
    font.header.hardblank.toString

  /** The FIGure represented with lines */
  lazy val lines: Seq[SubLines] =
    columns.map(_.toSublines)

  /** Lines stripped of their hardblanks */
  lazy val cleanLines: Seq[SubLines] =
    lines.map(_.replace(hardblank, " "))

  /** Columns stripped of their hardblanks */
  lazy val cleanColumns: Seq[SubColumns] =
    columns.map(_.replace(hardblank, " "))

  /** The max width of the FIGure */
  val width: Int =
    lines
      .flatMap(_.value.map(_.length))
      .maxOption
      .getOrElse(0)
}

object FIGure {
  def apply(font: FIGfont): FIGure =
    FIGure(font.zero, font)

  def apply(char: FIGcharacter, font: FIGfont): FIGure =
    FIGure(font, char.name.toString, Seq(char.columns))

  def apply(char: Char, font: FIGfont): FIGure =
    FIGure(font, char.toString, Seq(font(char).columns))
}
