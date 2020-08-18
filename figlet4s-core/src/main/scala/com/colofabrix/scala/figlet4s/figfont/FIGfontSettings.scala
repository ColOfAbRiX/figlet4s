package com.colofabrix.scala.figlet4s.figfont

import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters.{ HorizontalLayout, PrintDirection, VerticalLayout }

/**
 * Settings of a FIGfont
 */
final case class FIGfontSettings(
    hLayout: HorizontalLayout,
    vLayout: VerticalLayout,
    printDirection: PrintDirection,
)
