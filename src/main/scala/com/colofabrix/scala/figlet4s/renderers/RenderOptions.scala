package com.colofabrix.scala.figlet4s.renderers

import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

final case class RenderOptions(
    layout: Option[HorizontalLayout] = None,
    maxWidth: Option[Int] = None,
)
