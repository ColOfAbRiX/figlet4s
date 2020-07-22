package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters.FullWidthHorizontalLayout
import com.colofabrix.scala.figlet4s.RenderOptionsBuilder

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Main extends App {
  RenderOptionsBuilder("Fabrizio & Claire")
    .withInternalFont("alligator")
    .unsafeRender()
    .print()
}
