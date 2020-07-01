package com.colofabrix.scala.figlet4s.renderers

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

/**
 * Renders a String with the specified FIGfont using the Full Width layout
 */
final class FullWidthRenderer extends HorizontalTextRenderer[FullWidthHorizontalLayout.type] {

  /**
   * Appends two FIGures using the rule of the current layout
   */
  def append(first: FIGure, second: FIGure): FIGure =
    first.appendMappedLines(second)(_ + _)

}

object FullWidthRenderer {
  def render(text: String, font: FIGfont, options: RenderOptions): FIGure =
    new FullWidthRenderer().render(text, font, options)

  def render(text: String, font: FIGfont): FIGure =
    new FullWidthRenderer().render(text, font)
}
