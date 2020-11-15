package com.colofabrix.scala.figlet4s.figfont

import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters.{ HorizontalLayout, PrintDirection, VerticalLayout }

/**
 * Default settings of a FIGfont
 *
 * @param hLayout        The default horizontal layout of the FIGfont
 * @param vLayout        The default vertical layout of the FIGfont
 * @param printDirection The default print direction of the FIGfont
 */
final case class FIGfontSettings(
    hLayout: HorizontalLayout,
    vLayout: VerticalLayout,
    printDirection: PrintDirection,
)
