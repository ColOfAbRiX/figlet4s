package com.colofabrix.scala.figlet4s.renderes

import cats.implicits._
import cats.kernel.Monoid
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import cats.Foldable

/**
 * Renders a String for the horizontal direction with the specified FIGfont
 */
trait HorizontalTextRenderer[A <: HorizontalLayout] {
  /** Typeclass that folds FIGures */
  implicit protected def M: Monoid[FIGure]

  /** The font used to render the string */
  def font: FIGfont

  /** Renders a String into a FIGure */
  def render(text: String): FIGure =
    Foldable[Vector].fold {
      text.map(FIGure(_, font)).toVector
    }
}
