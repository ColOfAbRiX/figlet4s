package com.colofabrix.scala.figlet4s.testutils.originalfiglet

import com.colofabrix.scala.figlet4s.options._

/** The combination of text, font and rendering settings identifying a single rendering test */
final case class TestRenderOptions(
    renderText: String,
    fontName: String,
    horizontalLayout: HorizontalLayout,
    printDirection: PrintDirection,
    justification: Justification,
)
