package com.colofabrix.scala.figlet4s.figfont

import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters.FullLayout._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters.OldLayout._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters._
import com.colofabrix.scala.figlet4s.utils._

/**
 * Parameters and configuration settings used by FIGfonts.
 *
 * These settings are a Scala-friendly mapping of the FIGheaderParameters meant for internal processing but generally
 * discouraged for the end-user.
 */
private[figlet4s] object FIGfontParameters {
  /**
   * Horizontal Layout
   */
  sealed trait HorizontalLayout extends ADT

  object HorizontalLayout {
    /** Use full width horizontal layout */
    final case object FullWidthHorizontalLayout extends HorizontalLayout
    /** Use horizontal fitting (kerning) layout */
    final case object HorizontalFittingLayout extends HorizontalLayout
    /** Use universal horizontal smushing */
    final case object UniversalHorizontalSmushingLayout extends HorizontalLayout
    /** Use controlled horizontal smushing */
    final case class ControlledHorizontalSmushingLayout(rules: Vector[HorizontalSmushingRule]) extends HorizontalLayout

    /**
     * Interprets the header settings and returns the selected Horizontal Layout
     * Contrary to the FIGfont standard, if the header defines "fullLayout" then "oldLayout" is ignored
     */
    def fromHeader(header: FIGheader): FigletResult[HorizontalLayout] =
      (fromOldLayout(header), fromFullLayout(header))
        .mapN { (oldHorizontal, fullHorizontal) =>
          fullHorizontal
            .getOrElse(oldHorizontal)
        }

    /**
     * Interprets the "fullLayout" part of the header settings and returns the selected Horizontal Layout
     */
    def fromFullLayout(header: FIGheader): FigletResult[Option[HorizontalLayout]] =
      header
        .fullLayout
        .traverse { settings =>
          val selectedSmushingRules = settings intersect horizontalSmushingRules

          if (!settings.contains(UseHorizontalFitting) && !settings.contains(UseHorizontalSmushing))
            FullWidthHorizontalLayout.validNec
          else if (settings.contains(UseHorizontalFitting) && !settings.contains(UseHorizontalSmushing))
            HorizontalFittingLayout.validNec
          else if (settings.contains(UseHorizontalSmushing) && selectedSmushingRules.size =!= 0)
            HorizontalSmushingRule.fromFullLayout(header).map(ControlledHorizontalSmushingLayout)
          else
            UniversalHorizontalSmushingLayout.validNec
        }

    /**
     * Interprets the "oldLayout" part of the header settings and returns the selected Horizontal Layout
     */
    def fromOldLayout(header: FIGheader): FigletResult[HorizontalLayout] = {
      val settings = header.oldLayout.toVector

      if (settings === Vector(OldFullWidthLayout))
        FullWidthHorizontalLayout.validNec
      else if (settings === Vector(OldHorizontalFittingLayout))
        HorizontalFittingLayout.validNec
      else if (!settings.contains(OldFullWidthLayout) && !settings.contains(OldHorizontalFittingLayout))
        HorizontalSmushingRule.fromOldLayout(header).map(ControlledHorizontalSmushingLayout)
      else
        FIGFontError(s"Couldn't convert layout settings found in header: ${settings.mkString(", ")}").invalidNec
    }
  }

  /**
   * Rules for Horizontal Smushing
   */
  sealed trait HorizontalSmushingRule extends ADT

  object HorizontalSmushingRule {
    /** Apply "equal" character horizontal smushing */
    final case object EqualCharacterHorizontalSmushing extends HorizontalSmushingRule
    /** Apply "underscore" horizontal smushing */
    final case object UnderscoreHorizontalSmushing extends HorizontalSmushingRule
    /** Apply "hierarchy" horizontal smushing */
    final case object HierarchyHorizontalSmushing extends HorizontalSmushingRule
    /** Apply "opposite pair" horizontal smushing rule 4 */
    final case object OppositePairHorizontalSmushing extends HorizontalSmushingRule
    /** Apply "big X" horizontal smushing rule 5 */
    final case object BigXHorizontalSmushing extends HorizontalSmushingRule
    /** Apply "hardblank" horizontal smushing rule 6 */
    final case object HardblankHorizontalSmushing extends HorizontalSmushingRule

    /**
     * Interprets the "fullLayout" header settings and returns the selected Horizontal Smushing Rules
     */
    def fromFullLayout(header: FIGheader): FigletResult[Vector[HorizontalSmushingRule]] =
      header
        .fullLayout
        .map { settings =>
          (settings.toVector intersect FullLayout.horizontalSmushingRules).collect {
            case FullLayout.EqualCharacterHorizontalSmushing => EqualCharacterHorizontalSmushing
            case FullLayout.UnderscoreHorizontalSmushing     => UnderscoreHorizontalSmushing
            case FullLayout.HierarchyHorizontalSmushing      => HierarchyHorizontalSmushing
            case FullLayout.OppositePairHorizontalSmushing   => OppositePairHorizontalSmushing
            case FullLayout.BigXHorizontalSmushing           => BigXHorizontalSmushing
            case FullLayout.HardblankHorizontalSmushing      => HardblankHorizontalSmushing
          }
        }
        .getOrElse(Vector.empty)
        .validNec

    /**
     * Interprets the "oldLayout" header settings and returns the selected Horizontal Smushing Rules
     */
    def fromOldLayout(header: FIGheader): FigletResult[Vector[HorizontalSmushingRule]] =
      Some(header.oldLayout.toVector)
        .map {
          _.collect {
            case OldLayout.OldEqualCharacterHorizontalSmushing => EqualCharacterHorizontalSmushing
            case OldLayout.OldUnderscoreHorizontalSmushing     => UnderscoreHorizontalSmushing
            case OldLayout.OldHierarchyHorizontalSmushing      => HierarchyHorizontalSmushing
            case OldLayout.OldOppositePairHorizontalSmushing   => OppositePairHorizontalSmushing
            case OldLayout.OldBigXHorizontalSmushing           => BigXHorizontalSmushing
            case OldLayout.OldHardblankHorizontalSmushing      => HardblankHorizontalSmushing
          }
        }
        .filter(_.nonEmpty)
        .map(_.validNec)
        .getOrElse(FIGFontError(s"The oldLayout setting doesn't include any horizontal smushing rule").invalidNec)
  }

  /**
   * Vertical Layout
   */
  sealed trait VerticalLayout extends ADT

  object VerticalLayout {
    /** Use full height vertical layout */
    final case object FullHeightVerticalLayout extends VerticalLayout
    /** Use vertical fitting layout */
    final case object VerticalFittingLayout extends VerticalLayout
    /** Use universal vertical smushing */
    final case object UniversalVerticalSmushingLayout extends VerticalLayout
    /** Use controlled vertical smushing */
    final case class ControlledVerticalSmushingLayout(rules: Vector[VerticalSmushingRules]) extends VerticalLayout

    /**
     * Interprets the header settings and returns the selected Vertical Layout
     */
    def fromHeader(header: FIGheader): FigletResult[VerticalLayout] =
      header
        .fullLayout
        .map { settings =>
          val selectedSmushingRules = settings intersect FullLayout.verticalSmushingRules

          if (!settings.contains(UseVerticalFitting) && !settings.contains(UseVerticalSmushing))
            FullHeightVerticalLayout.validNec
          else if (settings.contains(UseVerticalFitting) && !settings.contains(UseVerticalSmushing))
            VerticalFittingLayout.validNec
          else if (settings.contains(UseVerticalSmushing) && selectedSmushingRules.size =!= 0)
            VerticalSmushingRules.fromHeader(header).map(ControlledVerticalSmushingLayout)
          else
            UniversalVerticalSmushingLayout.validNec
        }
        .getOrElse(FullHeightVerticalLayout.validNec)
  }

  /**
   * Rules for Vertical Smushing
   */
  sealed trait VerticalSmushingRules extends ADT

  object VerticalSmushingRules {
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

    /**
     * Interprets the header settings and returns the selected Vertical Smushing Rules
     */
    def fromHeader(header: FIGheader): FigletResult[Vector[VerticalSmushingRules]] =
      header
        .fullLayout
        .map { settings =>
          (settings.toVector intersect FullLayout.verticalSmushingRules).collect {
            case FullLayout.EqualCharacterVerticalSmushing    => EqualCharacterVerticalSmushing
            case FullLayout.UnderscoreVerticalSmushing        => UnderscoreVerticalSmushing
            case FullLayout.HierarchyVerticalSmushing         => HierarchyVerticalSmushing
            case FullLayout.HorizontalLineVerticalSmushing    => HorizontalLineVerticalSmushing
            case FullLayout.VerticalLineVerticalSuperSmushing => VerticalLineVerticalSuperSmushing
          }
        }
        .getOrElse(Vector.empty)
        .validNec
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

    /**
     * Interprets the header settings and returns the selected PrintDirection
     */
    def fromHeader(header: FIGheader): PrintDirection =
      header.printDirection match {
        case Some(FIGheaderParameters.PrintDirection.LeftToRight) => LeftToRight
        case Some(FIGheaderParameters.PrintDirection.RightToLeft) => RightToLeft
        case None                                                 => LeftToRight
      }
  }
}
