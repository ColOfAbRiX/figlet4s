package com.colofabrix.scala.figlet4s.options

import com.colofabrix.scala.figlet4s.figfont.FIGfont
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters.HorizontalLayout

/**
 * Rendering options, including the FIGfont to use
 */
final case class RenderOptions(
    font: FIGfont,
    horizontalLayout: HorizontalLayout,
    maxWidth: Int,
)
