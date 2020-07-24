package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters.FullWidthHorizontalLayout
import com.colofabrix.scala.figlet4s.RenderOptionsBuilder
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters.HorizontalFittingLayout

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Main extends App {
  import ImpureBuilder._

  RenderOptionsBuilder("Fabrizio & Claire")
    .withInternalFont("alligator")
    .withHorizontalLayout(HorizontalFittingLayout)
    .unsafeRender()
    .print()
}
