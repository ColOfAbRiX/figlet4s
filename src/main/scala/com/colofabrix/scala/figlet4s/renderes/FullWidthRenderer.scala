package com.colofabrix.scala.figlet4s.renderes

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

/**
 * Renders a String with the specified FIGfont using the Full Width layout
 */
class FullWidthRenderer(val font: FIGfont, options: RenderOptions)
    extends HorizontalTextRenderer[FullWidthHorizontalLayout.type] { self =>

  /**
   * Appends two FIGures using the rule of the current layout
   */
  protected def append(first: FIGure, second: FIGure): FIGure =
    Option(appendInline(first, second))
      .filter(_.width <= options.maxWidth.getOrElse(Int.MaxValue))
      .getOrElse(appendNewLine(first, second))

  private def appendInline(first: FIGure, second: FIGure): FIGure = {
    val appended =
      (first.lines.last zip second.lines.last)
        .map { case (f, s) => f + s }

    first.copy(
      lines = first.lines.init ++ Vector(appended),
      value = first.value + second.value,
    )
  }

  private def appendNewLine(first: FIGure, second: FIGure): FIGure =
    first.copy(
      lines = first.lines.last +: Vector(second.lines.last),
      value = first.value + second.value,
    )
}

object FullWidthRenderer {
  def apply(font: FIGfont, options: RenderOptions = RenderOptions()) =
    new FullWidthRenderer(font, options)
}
