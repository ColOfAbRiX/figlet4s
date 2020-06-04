package com.colofabrix.scala.figlet4s.figfont

import com.colofabrix.scala.figlet4s.renderes._

final case class FIGstring private[figlet4s] (value: String, font: FIGfont) {
  override lazy val toString: String =
    value

  def render(): Vector[String] = FullWidthRenderer.process(this)
}

final object FIGstring {
  def apply(font: FIGfont): FIGstring =
    FIGstring("", font)

  def apply(value: String, font: FIGfont): FIGstring =
    FIGstring(value, font)
}
