package com.colofabrix.scala.figlet4s.renderers

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

class UniversalHorizontalSmushingRenderer extends HorizontalTextRenderer[UniversalHorizontalSmushingLayout.type] {
  /**
   * Appends two FIGures using the rule of the current layout
   */
  protected def append(first: FIGure, second: FIGure): FIGure = {
    ???
  }
}

object UniversalHorizontalSmushingRenderer {
  def render(text: String, font: FIGfont, options: RenderOptions): FIGure =
    new UniversalHorizontalSmushingRenderer().render(text, font, options)

  def render(text: String, font: FIGfont): FIGure =
    new UniversalHorizontalSmushingRenderer().render(text, font)
}
