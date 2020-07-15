package com.colofabrix.scala.figlet4s.renderers

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.renderers.MergeAction
import com.colofabrix.scala.figlet4s.renderers.MergeAction._

/**
 * Renders a String with the specified FIGfont using the Full Width layout
 */
class HorizontalFittingRenderer extends HorizontalTextRenderer[HorizontalFittingLayout.type] {
  /**
   * Appends two FIGures using the rule of the current layout
   */
  protected def appendColumns(first: SubColumns, second: SubColumns): SubColumns =
    MergeAction.process(first, second) {
      case (' ', ' ')  => Continue(' ')
      case (aStr, ' ') => Continue(aStr)
      case (' ', bStr) => Continue(bStr)
      case (_, _)      => Stop
    }
}

object HorizontalFittingRenderer {
  def render(text: String, font: FIGfont, options: RenderOptions): FIGure =
    new HorizontalFittingRenderer().render(text, font, options)

  def render(text: String, font: FIGfont): FIGure =
    new HorizontalFittingRenderer().render(text, font)
}
