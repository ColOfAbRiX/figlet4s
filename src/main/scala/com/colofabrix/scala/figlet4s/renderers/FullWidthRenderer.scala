package com.colofabrix.scala.figlet4s.renderers

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.renderers.MergeAction
import com.colofabrix.scala.figlet4s.renderers.MergeAction._

/**
 * Renders a String with the specified FIGfont using the Full Width layout
 */
final class FullWidthRenderer extends HorizontalTextRenderer[FullWidthHorizontalLayout.type] {
  /**
   * Appends two FIGures using the rule of the current layout
   */
  protected def appendColumns(first: SubColumns, second: SubColumns): SubColumns =
    MergeAction.process(first, second) {
      case (_, _) => Stop
    }
}

object FullWidthRenderer {
  def render(text: String, font: FIGfont, options: RenderOptions): FIGure =
    new FullWidthRenderer().render(text, font, options)

  def render(text: String, font: FIGfont): FIGure =
    new FullWidthRenderer().render(text, font)
}
