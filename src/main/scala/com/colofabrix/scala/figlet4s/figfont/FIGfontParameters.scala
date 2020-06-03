package com.colofabrix.scala.figlet4s.figfont

import cats.implicits._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters.OldLayout._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters.FullLayout._

/**
 * Parameters and configuration settings used by FIGfonts
 */
object FIGfontParameters {
  /**
   * Horizontal Layout
   */
  sealed trait HorizontalLayout

  object HorizontalLayout {

    def fromHeaderParams(header: FIGheader): FigletResult[HorizontalLayout] = {
      val fullHorizontal = fromFullLayout(header)
      val oldHorizontal  = fromOldlayout(header)
      ???
    }

    private def fromFullLayout(header: FIGheader): FigletResult[Option[HorizontalLayout]] =
      header
        .fullLayout
        .traverse { settings =>
          val selectedSmushingRules = settings intersect horizontalSmushingRules

          if (!settings.contains(UseHorizontalFitting) && !settings.contains(UseHorizontalSmushing))
            FullWidthHorizontalLayout.validNec
          else if (settings.contains(UseHorizontalFitting) && !settings.contains(UseHorizontalSmushing))
            HorizontalFittingLayout.validNec
          else if (settings.contains(UseHorizontalSmushing) && selectedSmushingRules.size != 0)
            HorizontalSmushingRules.fromHeaderParams(header).map(ControlledHorizontalSmushingLayout(_))
          else
            UniversalHorizontalSmushingLayout.validNec
        }

    private def fromOldlayout(header: FIGheader): FigletResult[HorizontalLayout] = {
      val settings = header.oldLayout
      if (settings.contains(OldFullWidthLayout))
        FullWidthHorizontalLayout.validNec
      else if (settings.contains(OldHorizontalFittingLayout))
        HorizontalFittingLayout.validNec
      else if (!settings.contains(OldFullWidthLayout) && !settings.contains(OldHorizontalFittingLayout))
        UniversalHorizontalSmushingLayout.validNec
      else
        FIGFontError(s"Couldn't convert layout settings found in header: ${settings.mkString(", ")}").invalidNec
    }
  }

  /** Use full width horizontal layout */
  final case object FullWidthHorizontalLayout extends HorizontalLayout

  /** Use horizontal fitting (kerning) layout */
  final case object HorizontalFittingLayout extends HorizontalLayout

  /** Use universal horizontal smushing */
  final case object UniversalHorizontalSmushingLayout extends HorizontalLayout

  /** Use controlled horizontal smushing */
  final case class ControlledHorizontalSmushingLayout(rules: Vector[HorizontalSmushingRules]) extends HorizontalLayout

  /**
   * Rules for Horizontal Smushing
   */
  sealed trait HorizontalSmushingRules

  object HorizontalSmushingRules {
    def fromHeaderParams(header: FIGheader): FigletResult[Vector[HorizontalSmushingRules]] = ???
  }

  /** Apply "equal" character horizontal smushing */
  final case object EqualCharacterHorizontalSmushing extends HorizontalSmushingRules

  /** Apply "underscore" horizontal smushing */
  final case object UnderscoreHorizontalSmushing extends HorizontalSmushingRules

  /** Apply "hierarchy" horizontal smushing */
  final case object HierarchyHorizontalSmushing extends HorizontalSmushingRules

  /** Apply "opposite pair" horizontal smushing rule 4 */
  final case object OppositePairHorizontalSmushing extends HorizontalSmushingRules

  /** Apply "big X" horizontal smushing rule 5 */
  final case object BigXHorizontalSmushing extends HorizontalSmushingRules

  /** Apply "hardblank" horizontal smushing rule 6 */
  final case object HardblankHorizontalSmushing extends HorizontalSmushingRules

  /**
   * Vertical Layout
   */
  sealed trait VerticalLayout

  object VerticalLayout {
    def fromHeaderParams(header: FIGheader): FigletResult[VerticalLayout] = {
      header
        .fullLayout
        .map { settings =>
          val selectedSmushingRules = settings intersect FullLayout.verticalSmushingRules

          if (!settings.contains(UseVerticalFitting) && !settings.contains(UseVerticalSmushing))
            FullHeightVerticalLayout.validNec
          else if (settings.contains(UseVerticalFitting) && !settings.contains(UseVerticalSmushing))
            VerticalFittingLayout.validNec
          else if (settings.contains(UseVerticalSmushing) && selectedSmushingRules.size != 0)
            VerticalSmushingRules.fromHeaderParams(header).map(ControlledVerticalSmushingLayout(_))
          else
            UniversalVerticalSmushingLayout.validNec
        }
        .getOrElse(FullHeightVerticalLayout.validNec)
    }
  }

  /** Use full height vertical layout */
  final case object FullHeightVerticalLayout extends VerticalLayout

  /** Use vertical fitting layout */
  final case object VerticalFittingLayout extends VerticalLayout

  /** Use universal vertical smushing */
  final case object UniversalVerticalSmushingLayout extends VerticalLayout

  /** Use controlled vertical smushing */
  final case class ControlledVerticalSmushingLayout(rules: Vector[VerticalSmushingRules]) extends VerticalLayout

  /**
   * Rules for Vertical Smushing
   */
  sealed trait VerticalSmushingRules

  object VerticalSmushingRules {
    def fromHeaderParams(header: FIGheader): FigletResult[Vector[VerticalSmushingRules]] = ???
  }

  /** Apply "equal" character vertical smushing */
  final case object EqualCharacterVerticalSmushing extends VerticalSmushingRules

  /** Apply "underscore" vertical smushing */
  final case object UnderscoreVerticalSmushing extends VerticalSmushingRules

  /** Apply "hierarchy" vertical smushing */
  final case object HierarchyVerticalSmushing extends VerticalSmushingRules

  /** Apply "horizontal line" vertical smushing */
  final case object HorizontalLineVerticalSmushing extends VerticalSmushingRules

  /** Apply "vertical line" vertical smushing */
  final case object VerticalLineVerticalSuperSmushing extends VerticalSmushingRules

}
