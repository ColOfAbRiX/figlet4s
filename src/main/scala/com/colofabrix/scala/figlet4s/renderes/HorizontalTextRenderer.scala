package com.colofabrix.scala.figlet4s.renderes

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

/**
 * Renders a String for the horizontal direction with the specified FIGfont
 */
trait HorizontalTextRenderer[A <: HorizontalLayout] {
  /** The font used to render the string */
  def font: FIGfont

  /** Renders a String into a FIGure */
  def render(text: String): FIGure =
    text
      .foldLeft(empty) {
        case (accumulator, current) => append(accumulator, FIGure(current, font))
      }

  /**
   * Creates an empty FIGure of the currently used FIGfont
   */
  protected def empty: FIGure = FIGure(font)

  /**
   * Appends two FIGures using the rule of the current layout
   */
  protected def append(first: FIGure, second: FIGure): FIGure
}
