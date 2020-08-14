package com.colofabrix.scala.figlet4s.figfont

import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters.FullLayout._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters.OldLayout._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters._

/**
 * Parameters and configuration settings used by FIGfonts.
 * It is a Scala-friendly mapping of the FIGheaderParameters and it's meant for internal use only
 */
object FIGfontParameters {
  /**
   * Horizontal Layout
   */
  sealed trait HorizontalLayout extends Product with Serializable

  object HorizontalLayout {

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
      val settings = header.oldLayout

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

  /** Use full width horizontal layout */
  final case object FullWidthHorizontalLayout extends HorizontalLayout

  /** Use horizontal fitting (kerning) layout */
  final case object HorizontalFittingLayout extends HorizontalLayout

  /** Use universal horizontal smushing */
  final case object UniversalHorizontalSmushingLayout extends HorizontalLayout

  /** Use controlled horizontal smushing */
  final case class ControlledHorizontalSmushingLayout(rules: Vector[HorizontalSmushingRule]) extends HorizontalLayout

  /**
   * Rules for Horizontal Smushing
   */
  sealed trait HorizontalSmushingRule extends Product with Serializable

  object HorizontalSmushingRule {
    /**
     * Interprets the "fullLayout" header settings and returns the selected Horizontal Smushing Rules
     */
    def fromFullLayout(header: FIGheader): FigletResult[Vector[HorizontalSmushingRule]] =
      header
        .fullLayout
        .map { settings =>
          (settings intersect FullLayout.horizontalSmushingRules).collect {
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
      Some(header.oldLayout)
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
   * Vertical Layout
   */
  sealed trait VerticalLayout extends Product with Serializable

  object VerticalLayout {
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
  sealed trait VerticalSmushingRules extends Product with Serializable

  object VerticalSmushingRules {
    /**
     * Interprets the header settings and returns the selected Vertical Smushing Rules
     */
    def fromHeader(header: FIGheader): FigletResult[Vector[VerticalSmushingRules]] =
      header
        .fullLayout
        .map { settings =>
          (settings intersect FullLayout.verticalSmushingRules).collect {
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
