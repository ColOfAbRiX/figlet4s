package com.colofabrix.scala.figlet4s.options

import com.colofabrix.scala.figlet4s.figfont._

/**
 * Option to chose the desired horizontal rendering layout
 */
sealed trait HorizontalLayout extends Product with Serializable

object HorizontalLayout {

  final case object FullWidth               extends HorizontalLayout
  final case object HorizontalFitting       extends HorizontalLayout
  final case object HorizontalSmushing      extends HorizontalLayout
  final case object ForceHorizontalSmushing extends HorizontalLayout

  /**
   * Converts the option HorizontalLayout into the FIGfont parameter HorizontalLayout
   */
  def toInternalLayout(layout: HorizontalLayout, font: FIGfont): FIGfontParameters.HorizontalLayout = {
    import FIGfontParameters._

    layout match {
      case FullWidth          => FullWidthHorizontalLayout
      case HorizontalFitting  => HorizontalFittingLayout
      case HorizontalSmushing => font.hLayout
      case ForceHorizontalSmushing =>
        font.hLayout match {
          case ControlledHorizontalSmushingLayout(_) => font.hLayout
          case _                                     => UniversalHorizontalSmushingLayout
        }
    }
  }

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

  /**
   * Converts the option HorizontalLayout into the FIGheader parameter HorizontalLayout
   */
  def toInternalLayout(layout: PrintDirection): FIGheaderParameters.PrintDirection =
    layout match {
      case LeftToRight => FIGheaderParameters.PrintDirection.LeftToRight
      case RightToLeft => FIGheaderParameters.PrintDirection.RightToLeft
    }

}
