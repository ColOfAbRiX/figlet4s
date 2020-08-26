package com.colofabrix.scala.figlet4s.options

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.utils._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters.{ HorizontalLayout => FontHorizontalLayout }
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters.{ PrintDirection => FontDirection }

/**
 * Option to chose the desired horizontal rendering layout
 */
sealed trait HorizontalLayout extends ADT

object HorizontalLayout {

  /**
   * Full width. Display all FIGcharacters at their full width, which may be fixed or variable, depending on the font
   */
  final case object FullWidth extends HorizontalLayout

  /**
   * Kerning. As many blanks as possible are removed between FIGcharacters, so that they touch, but the FIGcharacters
   * are not smushed.
   */
  final case object HorizontalFitting extends HorizontalLayout

  /**
   * Smushing. The FIGcharacters are displayed as close together as possible, and overlapping sub-characters are
   * removed. Exactly which sub-characters count as overlapping depends on the font's layout mode, which is defined by
   * the font's author. It will not smush a font whose author specified kerning or full width as the default layout mode
   */
  final case object HorizontalSmushing extends HorizontalLayout

  /**
   * Forced smushing. The FIGcharacters are displayed as close together as possible, and overlapping sub-characters are
   * removed. Exactly which sub-characters count as overlapping depends on the font's layout mode, which is defined by
   * the font's author. It will attempt to smush the character even if the font author specified kerning or full width
   * as the default layout mode.
   */
  final case object ForceHorizontalSmushing extends HorizontalLayout

  /**
   * Use the default value specified in the FIGfont
   */
  final case object FontDefault extends HorizontalLayout

  /**
   * Converts the option HorizontalLayout into the FIGfont parameter HorizontalLayout
   */
  def toInternalLayout(font: FIGfont): PartialFunction[HorizontalLayout, FontHorizontalLayout] = {
    case FontDefault        => font.settings.hLayout
    case FullWidth          => FontHorizontalLayout.FullWidthHorizontalLayout
    case HorizontalFitting  => FontHorizontalLayout.HorizontalFittingLayout
    case HorizontalSmushing => font.settings.hLayout
    case ForceHorizontalSmushing =>
      font.settings.hLayout match {
        case FontHorizontalLayout.ControlledHorizontalSmushingLayout(_) =>
          font.settings.hLayout
        case _ =>
          FontHorizontalLayout.UniversalHorizontalSmushingLayout
      }
  }

}

/**
 * Option to choose the rendering direction
 */
sealed trait PrintDirection extends ADT

object PrintDirection {

  /** Render left-to-right */
  final case object LeftToRight extends PrintDirection
  /** Render right-to-left */
  final case object RightToLeft extends PrintDirection
  /** Use the default value specified in the FIGfont */
  final case object FontDefault extends PrintDirection

  /**
   * Converts the option PrintDirection into the FIGfont parameter PrintDirection
   */
  def toInternalLayout(font: FIGfont): PartialFunction[PrintDirection, FontDirection] = {
    case LeftToRight => FontDirection.LeftToRight
    case RightToLeft => FontDirection.RightToLeft
    case FontDefault => font.settings.printDirection
  }

}

/**
 * Option to choose the justification of the text (TODO)
 */
sealed trait Justification extends ADT

object Justification {

  /** Centers the output horizontally */
  final case object Center extends Justification
  /** Makes the output flush-left */
  final case object FlushLeft extends Justification
  /** Makes it flush-right */
  final case object FlushRight extends Justification
  /** Use the default value specified in the FIGfont */
  final case object FontDefault extends Justification

}