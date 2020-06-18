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
  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  val width: Int = lines.head.head.size

  /**
   * Cached access to the last line of the FIGure
   */
  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  lazy val lastLine: FIGline = lines.last

  /**
   * Lines stripped of their hardblanks
   */
  val cleanLines: Vector[FIGline] =
    Nested(lines)
      .map(_.replace(font.header.hardblank.toString, " "))
      .value
}

object FIGure {
  type FIGline = Vector[String]

  def apply(font: FIGfont): FIGure =
    FIGure(font.zero, font)

  def apply(char: FIGcharacter, font: FIGfont): FIGure =
    FIGure(font, char.name.toString, Vector(char.lines))

  def apply(char: Char, font: FIGfont): FIGure =
    FIGure(font, char.toString, Vector(font(char).lines))
}
