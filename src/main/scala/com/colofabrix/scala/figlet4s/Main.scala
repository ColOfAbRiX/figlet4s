package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.renderers.RenderOptions
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters.FullWidthHorizontalLayout
import com.colofabrix.scala.figlet4s.renderers.RenderOptionsBuilder

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Main extends App {
  private val optionBuilder = RenderOptionsBuilder().withInternalFont("alligator")
  Figlet4s.renderString("Fabrizio & Claire", optionBuilder).print()
}
