package com.colofabrix.scala.figlet4s

import unsafe._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.RenderOptionsBuilder

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Main extends App {

  RenderOptionsBuilder("Fabrizio & Claire")
    .withInternalFont("alligator")
    .withHorizontalLayout(HorizontalFittingLayout)
    .unsafeRender()
    .print()

}
