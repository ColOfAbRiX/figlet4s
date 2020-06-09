package com.colofabrix.scala.figlet4s.figfont

import com.colofabrix.scala.figlet4s.figfont.FIGure._

/**
 * A FIGure that is a rendered String with a specific FIGfont and built built from multiple FIGcharacters
 */
final case class FIGure private[figlet4s] (
    font: FIGfont,
    value: String,
    lines: Vector[FIGline],
)

object FIGure {
  type FIGline = Vector[String]

  def apply(font: FIGfont): FIGure =
    FIGure(font.zero, font)

  def apply(char: FIGcharacter, font: FIGfont): FIGure =
    FIGure(font, char.name.toString, Vector(char.lines))

  def apply(char: Char, font: FIGfont): FIGure =
    FIGure(font, char.toString, Vector(font(char).lines))
}
