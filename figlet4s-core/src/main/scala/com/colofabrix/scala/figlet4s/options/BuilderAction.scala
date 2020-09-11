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

  final case object DefaultFontAction       extends BuilderAction with FontTag
  final case object DefaultHorizontalLayout extends BuilderAction with HorizontalLayoutTag
  final case object DefaultJustification    extends BuilderAction with JustificationTag
  final case object DefaultMaxWidthAction   extends BuilderAction with MaxWidthActionTag
  final case object DefaultPrintDirection   extends BuilderAction with PrintDirectionTag

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

  sealed trait FontTag
  sealed trait HorizontalLayoutTag
  sealed trait JustificationTag
  sealed trait MaxWidthActionTag
  sealed trait PrintDirectionTag
  sealed trait TextTag

  /** Determines if two actions belong to the same group by using Action Tags */
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
