package com.colofabrix.scala.figlet4s.options

import com.colofabrix.scala.figlet4s.figfont._

/**
 * Rendering options, including the FIGfont to use
 *
 * @param font             The FIGfont to use to render teh text
 * @param maxWidth         The maximum width of rendered text
 * @param horizontalLayout The desired horizontal layout to render the text
 * @param printDirection   The print direction
 * @param justification    The text justification
 */
final case class RenderOptions(
    font: FIGfont,
    maxWidth: Int,
    horizontalLayout: HorizontalLayout,
    printDirection: PrintDirection,
    justification: Justification,
)
