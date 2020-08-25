package com.colofabrix.scala.figlet4s.figfont

/**
 * A FIGure that is a rendered String with a specific FIGfont and built built from multiple FIGcharacters
 */
final case class FIGure private[figlet4s] (
    font: FIGfont,
    value: String,
    columns: Vector[SubColumns],
) {
  private val hardblank =
    font.header.hardblank.toString

  /** The FIGure represented with lines */
  lazy val lines: Vector[SubLines] =
    columns.map(_.toSublines)

  /** Lines stripped of their hardblanks */
  val cleanLines: Vector[SubLines] =
    lines.map(_.replace(hardblank, " "))

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
    FIGure(font, char.name.toString, Vector(char.columns))

  def apply(char: Char, font: FIGfont): FIGure =
    FIGure(font, char.toString, Vector(font(char).columns))
}
