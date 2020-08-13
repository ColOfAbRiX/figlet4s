package com.colofabrix.scala.figlet4s.options

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters.HorizontalLayout

/**
 * Rendering options, including the FIGfont to use
 */
final case class RenderOptions(
    font: FIGfont,
    maxWidth: Int,
    horizontalLayout: HorizontalLayout,
    horizontalLayout2: HorizontalLayout2,
    printDirection: PrintDirection,
) {
  override def toString: String =
    s"RenderOptions(font=${font.name}, " +
    s"maxWidth=$maxWidth, " +
    s"horizontalLayout=$horizontalLayout, " +
    s"printDirection=$printDirection)"
}

/**
 * Option to chose the desired horizontal rendering layout
 */
sealed trait HorizontalLayout2 extends Product with Serializable

object HorizontalLayout2 {

  final case object FullWidth               extends HorizontalLayout2
  final case object HorizontalFitting       extends HorizontalLayout2
  final case object HorizontalSmushing      extends HorizontalLayout2
  final case object ForceHorizontalSmushing extends HorizontalLayout2

}

/**
 * Option to choose the rendering direction
 */
sealed trait PrintDirection extends Product with Serializable

object PrintDirection {

  /** Render left-to-right */
  final case object LeftToRight extends PrintDirection
  /** Render right-to-left */
  final case object RightToLeft extends PrintDirection

}
