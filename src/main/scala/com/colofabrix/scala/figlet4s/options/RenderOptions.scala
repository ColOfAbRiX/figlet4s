package com.colofabrix.scala.figlet4s.options

import com.colofabrix.scala.figlet4s.figfont._

/**
 * Rendering options, including the FIGfont to use
 */
final case class RenderOptions(
    font: FIGfont,
    maxWidth: Int,
    horizontalLayout: FIGfontParameters.HorizontalLayout,
    printDirection: FIGfontParameters.PrintDirection,
) {
  override def toString: String =
    s"RenderOptions(font=${font.name}, " +
    s"maxWidth=$maxWidth, " +
    s"horizontalLayout=$horizontalLayout, " +
    s"printDirection=$printDirection)"
}
