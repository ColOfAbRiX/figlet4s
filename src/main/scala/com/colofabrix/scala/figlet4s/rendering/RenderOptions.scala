package com.colofabrix.scala.figlet4s.rendering

import com.colofabrix.scala.figlet4s.figfont.FIGfont
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

/**
 * Rendering options, including the FIGfont to use
 */
final case class RenderOptions(
    font: FIGfont,
    horizontalLayout: HorizontalLayout,
    maxWidth: Int,
)
