package com.colofabrix.scala.figlet4s.options

import com.colofabrix.scala.figlet4s.figfont._

/**
 * Rendering options, including the FIGfont to use
 */
final case class RenderOptions(
    font: FIGfont,
    maxWidth: Int = Int.MaxValue,
    horizontalLayout: HorizontalLayout = HorizontalLayout.FontDefault,
    printDirection: PrintDirection = PrintDirection.FontDefault,
    justification: Justification = Justification.FontDefault,
) {
  override def toString: String =
    s"RenderOptions(font=${font.name}, " +
    s"maxWidth=$maxWidth, " +
    s"horizontalLayout=$horizontalLayout, " +
    s"printDirection=$printDirection, " +
    s"justification=$justification)"
}
