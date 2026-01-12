package com.colofabrix.scala.figlet4s.options

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.utils._
import scala.io._

/**
 * The BuilderAction data structure is used to defer the call of the API at rendering time, to avoid dealing with
 * calls that might fail (like loading a font) and the management of effect, while the user is constructing the the
 * options.
 */
private[figlet4s] trait BuilderAction extends ADT

private[figlet4s] object BuilderAction {

  //  Default Build Actions  //

  case object DefaultFontAction       extends BuilderAction with FontTag
  case object DefaultHorizontalLayout extends BuilderAction with HorizontalLayoutTag
  case object DefaultJustification    extends BuilderAction with JustificationTag
  case object DefaultMaxWidthAction   extends BuilderAction with MaxWidthActionTag
  case object DefaultPrintDirection   extends BuilderAction with PrintDirectionTag

  //  Builder Actions  //

  final case class LoadFontAction(fontPath: String, codec: Codec) extends BuilderAction with FontTag
  final case class LoadInternalFontAction(fontName: String)       extends BuilderAction with FontTag
  final case class SetFontAction(font: FIGfont)                   extends BuilderAction with FontTag
  final case class SetHorizontalLayout(layout: HorizontalLayout)  extends BuilderAction with HorizontalLayoutTag
  final case class SetJustification(justification: Justification) extends BuilderAction with JustificationTag
  final case class SetMaxWidthAction(maxWidth: Int)               extends BuilderAction with MaxWidthActionTag
  final case class SetPrintDirection(direction: PrintDirection)   extends BuilderAction with PrintDirectionTag
  final case class SetTextAction(text: String)                    extends BuilderAction with TextTag

  //  Action Tags  //

  sealed trait ActionTag
  sealed trait FontTag             extends ActionTag
  sealed trait HorizontalLayoutTag extends ActionTag
  sealed trait JustificationTag    extends ActionTag
  sealed trait MaxWidthActionTag   extends ActionTag
  sealed trait PrintDirectionTag   extends ActionTag
  sealed trait TextTag             extends ActionTag

  /**
   * Determines if two actions belong to the same group by using ActionTag
   *
   * @param a The first BuilderAction to check
   * @param b The true BuilderAction to check
   * @return A Boolean value where `true` means the two BuilderAction belong to the same ActionTag
   */
  def sameGroupAs(a: BuilderAction)(b: BuilderAction): Boolean =
    (a, b) match {
      case (_: FontTag, _: FontTag)                         => true
      case (_: HorizontalLayoutTag, _: HorizontalLayoutTag) => true
      case (_: JustificationTag, _: JustificationTag)       => true
      case (_: MaxWidthActionTag, _: MaxWidthActionTag)     => true
      case (_: PrintDirectionTag, _: PrintDirectionTag)     => true
      case (_: TextTag, _: TextTag)                         => true
      case _                                                => false
    }

}
