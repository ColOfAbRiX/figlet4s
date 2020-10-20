package com.colofabrix.scala.figlet4s.figfont

import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
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
    final case object FullWidth extends HorizontalLayout
    /** Use horizontal fitting (kerning) layout */
    final case object HorizontalFitting extends HorizontalLayout
    /** Use universal horizontal smushing */
    final case object UniversalSmushing extends HorizontalLayout
    /** Use controlled horizontal smushing */
    final case class ControlledSmushing(rules: Vector[HorizontalSmushingRule]) extends HorizontalLayout

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
          val selectedSmushingRules = settings intersect FullLayout.horizontalSmushingRules

          if (!settings.contains(FullLayout.HorizontalFitting) && !settings.contains(FullLayout.HorizontalSmushing))
            FullWidth.validNec
          else if (settings.contains(FullLayout.HorizontalFitting) && !settings.contains(FullLayout.HorizontalSmushing))
            HorizontalFitting.validNec
          else if (settings.contains(FullLayout.HorizontalSmushing) && selectedSmushingRules.size =!= 0)
            HorizontalSmushingRule.fromFullLayout(header).map(ControlledSmushing)
          else
            UniversalSmushing.validNec
        }

    /**
     * Interprets the "oldLayout" part of the header settings and returns the selected Horizontal Layout
     */
    def fromOldLayout(header: FIGheader): FigletResult[HorizontalLayout] = {
      val settings = header.oldLayout.toVector

      if (settings === Vector(OldLayout.FullWidth))
        FullWidth.validNec
      else if (settings === Vector(OldLayout.HorizontalFitting))
        HorizontalFitting.validNec
      else if (!settings.contains(OldLayout.FullWidth) && !settings.contains(OldLayout.HorizontalFitting))
        HorizontalSmushingRule.fromOldLayout(header).map(ControlledSmushing)
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
    final case object EqualCharacter extends HorizontalSmushingRule
    /** Apply "underscore" horizontal smushing */
    final case object Underscore extends HorizontalSmushingRule
    /** Apply "hierarchy" horizontal smushing */
    final case object Hierarchy extends HorizontalSmushingRule
    /** Apply "opposite pair" horizontal smushing rule 4 */
    final case object OppositePair extends HorizontalSmushingRule
    /** Apply "big X" horizontal smushing rule 5 */
    final case object BigX extends HorizontalSmushingRule
    /** Apply "hardblank" horizontal smushing rule 6 */
    final case object Hardblank extends HorizontalSmushingRule

    /**
     * Interprets the "fullLayout" header settings and returns the selected Horizontal Smushing Rules
     */
    def fromFullLayout(header: FIGheader): FigletResult[Vector[HorizontalSmushingRule]] =
      header
        .fullLayout
        .map { settings =>
          (settings.toVector intersect FullLayout.horizontalSmushingRules).collect {
            case FullLayout.EqualCharacterHorizontalSmushing => EqualCharacter
            case FullLayout.UnderscoreHorizontalSmushing     => Underscore
            case FullLayout.HierarchyHorizontalSmushing      => Hierarchy
            case FullLayout.OppositePairHorizontalSmushing   => OppositePair
            case FullLayout.BigXHorizontalSmushing           => BigX
            case FullLayout.HardblankHorizontalSmushing      => Hardblank
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
            case OldLayout.EqualCharacterSmushing => EqualCharacter
            case OldLayout.UnderscoreSmushing     => Underscore
            case OldLayout.HierarchySmushing      => Hierarchy
            case OldLayout.OppositePairSmushing   => OppositePair
            case OldLayout.BigXSmushing           => BigX
            case OldLayout.HardblankSmushing      => Hardblank
          }
        }
        .withFilter(_.nonEmpty)
        .map(_.validNec)
        .getOrElse(FIGFontError(s"The oldLayout setting doesn't include any horizontal smushing rule").invalidNec)
  }

  /**
   * Vertical Layout
   */
  sealed trait VerticalLayout extends ADT

  object VerticalLayout {
    /** Use full height vertical layout */
    final case object FullHeight extends VerticalLayout
    /** Use vertical fitting layout */
    final case object VerticalFitting extends VerticalLayout
    /** Use universal vertical smushing */
    final case object UniversalSmushing extends VerticalLayout
    /** Use controlled vertical smushing */
    final case class ControlledSmushing(rules: Vector[VerticalSmushingRules]) extends VerticalLayout

    /**
     * Interprets the header settings and returns the selected Vertical Layout
     */
    def fromHeader(header: FIGheader): FigletResult[VerticalLayout] =
      header
        .fullLayout
        .map { settings =>
          val selectedSmushingRules = settings intersect FullLayout.verticalSmushingRules

          if (!settings.contains(FullLayout.VerticalFitting) && !settings.contains(FullLayout.VerticalSmushing))
            FullHeight.validNec
          else if (settings.contains(FullLayout.VerticalFitting) && !settings.contains(FullLayout.VerticalSmushing))
            VerticalFitting.validNec
          else if (settings.contains(FullLayout.VerticalSmushing) && selectedSmushingRules.size =!= 0)
            VerticalSmushingRules.fromHeader(header).map(ControlledSmushing)
          else
            UniversalSmushing.validNec
        }
        .getOrElse(FullHeight.validNec)
  }

  /**
   * Rules for Vertical Smushing
   */
  sealed trait VerticalSmushingRules extends ADT

  object VerticalSmushingRules {
    /** Apply "equal" character vertical smushing */
    final case object EqualCharacter extends VerticalSmushingRules
    /** Apply "underscore" vertical smushing */
    final case object Underscore extends VerticalSmushingRules
    /** Apply "hierarchy" vertical smushing */
    final case object Hierarchy extends VerticalSmushingRules
    /** Apply "horizontal line" vertical smushing */
    final case object HorizontalLine extends VerticalSmushingRules
    /** Apply "vertical line" vertical smushing */
    final case object VerticalLineSupersmushing extends VerticalSmushingRules

    /**
     * Interprets the header settings and returns the selected Vertical Smushing Rules
     */
    def fromHeader(header: FIGheader): FigletResult[Vector[VerticalSmushingRules]] =
      header
        .fullLayout
        .map { settings =>
          (settings.toVector intersect FullLayout.verticalSmushingRules).collect {
            case FullLayout.EqualCharacterVerticalSmushing => EqualCharacter
            case FullLayout.UnderscoreVerticalSmushing     => Underscore
            case FullLayout.HierarchyVerticalSmushing      => Hierarchy
            case FullLayout.HorizontalLineVerticalSmushing => HorizontalLine
            case FullLayout.VerticalLineSupersmushing      => VerticalLineSupersmushing
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
