package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.unsafe._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Main extends App {

  Figlet4s
    .builder("Fabrizio & Claire")
    .withInternalFont("alligator")
    .withHorizontalLayout(HorizontalFittingLayout)
    .render()
    .print()

}
