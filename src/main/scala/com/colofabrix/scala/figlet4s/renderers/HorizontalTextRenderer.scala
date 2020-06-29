package com.colofabrix.scala.figlet4s.renderers

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

/**
 * Renders a String for the horizontal direction with the specified FIGfont
 */
trait HorizontalTextRenderer[A <: HorizontalLayout] {
  /**
   * Renders a String into a FIGure
   */
  def render(text: String, font: FIGfont): FIGure =
    render(text, font, RenderOptions())

  /**
   * Renders a String into a FIGure
   */
  def render(text: String, font: FIGfont, options: RenderOptions): FIGure =
    text
      .map(FIGure(_, font))
      .foldLeft(empty(font)) {
        case (accumulator, current) =>
          Option(append(accumulator, current))
            .filter(_.width <= options.maxWidth.getOrElse(Int.MaxValue))
            .getOrElse(accumulator.append(current))
      }

  /**
   * Creates an empty FIGure of the currently used FIGfont
   */
  protected def empty(font: FIGfont): FIGure = FIGure(font)

  /**
   * Appends two FIGures using the rule of the current layout
   */
  protected def append(first: FIGure, second: FIGure): FIGure
}
