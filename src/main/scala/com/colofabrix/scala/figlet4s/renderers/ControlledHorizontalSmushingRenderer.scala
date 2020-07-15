package com.colofabrix.scala.figlet4s.renderers

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

class ControlledHorizontalSmushingRenderer extends HorizontalTextRenderer[ControlledHorizontalSmushingLayout] {
  /**
   * Appends two FIGures using the rule of the current layout
   */
  protected def appendColumns(first: SubColumns, second: SubColumns): SubColumns =
    ???
}

object ControlledHorizontalSmushingRenderer {
  def render(text: String, font: FIGfont, options: RenderOptions): FIGure =
    new ControlledHorizontalSmushingRenderer().render(text, font, options)

  def render(text: String, font: FIGfont): FIGure =
    new ControlledHorizontalSmushingRenderer().render(text, font)
}
