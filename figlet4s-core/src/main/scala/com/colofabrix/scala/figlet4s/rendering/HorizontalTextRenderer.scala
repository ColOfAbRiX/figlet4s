package com.colofabrix.scala.figlet4s.rendering

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options.{ HorizontalLayout => ClientHorizontalLayout, RenderOptions }
import com.colofabrix.scala.figlet4s.rendering.MergeAction._
import com.colofabrix.scala.figlet4s.rendering.Rendering._

/**
 * Renderer for Horizontal Layouts
 */
private[figlet4s] object HorizontalTextRenderer {
  /**
   * Renders a String into a FIGure for a given FIGfont and options
   */
  def render(text: String, options: RenderOptions): FIGure = {
    val chosenLayout  = ClientHorizontalLayout.toInternalLayout(options.font)(options.horizontalLayout)
    val mergeOptions  = Rendering.MergeOptions(options.font.header.hardblank)
    val mergeStrategy = layout2mergeStrategy(mergeOptions)(chosenLayout)
    horizontalRender(text, options, mergeStrategy)
  }

  /**
   * Returns the merge strategy function given a layout
   */
  private def layout2mergeStrategy(options: Rendering.MergeOptions): HorizontalLayout => MergeStrategy = {
    case HorizontalLayout.FullWidth                 => fullWidthStrategy(options)
    case HorizontalLayout.HorizontalFitting         => horizontalFittingStrategy(options)
    case HorizontalLayout.UniversalSmushing         => universalHorizontalSmushingStrategy(options)
    case HorizontalLayout.ControlledSmushing(rules) => controlledHorizontalSmushingStrategy(options, rules)
  }

  /**
   * Encodes the Full Width horizontal layout
   */
  private def fullWidthStrategy(options: Rendering.MergeOptions): MergeStrategy =
    mergeColumnWith(options) {
      case (_, _) => Stop
    }

  /**
   * Encodes the Horizontal Fitting horizontal layout
   */
  private def horizontalFittingStrategy(options: Rendering.MergeOptions): MergeStrategy =
    mergeColumnWith(options) {
      case (aChar, ' ') => Continue(aChar)
      case (' ', bChar) => Continue(bChar)
      case (_, _)       => Stop
    }

  /**
   * Encodes the Universal Horizontal Smushing horizontal layout
   */
  private def universalHorizontalSmushingStrategy(options: Rendering.MergeOptions): MergeStrategy =
    mergeColumnWith(options) {
      case (aChar, ' ') => Continue(aChar)
      case (' ', bChar) => Continue(bChar)
      case (aChar, bChar) if bChar === options.hardblank =>
        if (aChar === options.hardblank) Stop else CurrentLast(aChar)
      case (_, bChar) => CurrentLast(bChar)
    }

  /**
   * Encodes the Controlled Horizontal Smushing horizontal layout
   */
  private def controlledHorizontalSmushingStrategy(
      options: Rendering.MergeOptions,
      rules: Vector[HorizontalSmushingRule],
  ): MergeStrategy =
    mergeColumnWith(options) {
      case (aChar, ' ') => Continue(aChar)
      case (' ', bChar) => Continue(bChar)
      case (aChar, bChar) =>
        rules
          .map(rule2smushingStrategy(options))
          .map { f =>
            f(aChar, bChar)
          }
          .collectFirst { case Some(value) => CurrentLast(value) }
          .getOrElse(Stop: MergeAction[Char])
    }

  /**
   * Returns a smushing strategy function given the smushing rule
   */
  private def rule2smushingStrategy(options: Rendering.MergeOptions): HorizontalSmushingRule => SmushingStrategy = {
    case HorizontalSmushingRule.EqualCharacter => equalCharacterSmushingRule(options.hardblank)
    case HorizontalSmushingRule.Underscore     => underscoreSmushingRule
    case HorizontalSmushingRule.Hierarchy      => hierarchySmushingRule
    case HorizontalSmushingRule.OppositePair   => oppositePairSmushingRule
    case HorizontalSmushingRule.BigX           => bigXSmushingRule
    case HorizontalSmushingRule.Hardblank      => hardblankSmushingRule(options.hardblank)
  }

  /**
   * Two sub-characters are smushed into a single sub-character if they are the same. This rule does not smush
   * hardblanks.
   */
  private def equalCharacterSmushingRule(hardblank: Char): SmushingStrategy = { (a, b) =>
    if (a === b && a =!= hardblank) Some(a) else None
  }

  /**
   * An underscore ("_") will be replaced by any of: "|", "/", "\", "[", "]", "{", "}", "(", ")", "<" or ">".
   */
  private def underscoreSmushingRule: SmushingStrategy = { (a, b) =>
    val replaceWith = "|/\\[]{}()<>"
    (a, b) match {
      case ('_', _) if replaceWith.contains(b) => Some(b)
      case (_, '_') if replaceWith.contains(a) => Some(a)
      case _                                   => None
    }
  }

  /**
   * A hierarchy of six classes is used: "|", "/\", "[]", "{}", "()", and "<>". When two smushing sub-characters are
   * from different classes, the one from the latter class will be used.
   */
  private def hierarchySmushingRule: SmushingStrategy = { (a, b) =>
    val classes = Vector("|", "/\\", "[]", "{}", "()", "<>")
    val aClass  = classes.indexWhere(_.contains(a))
    val bClass  = classes.indexWhere(_.contains(b))

    if (aClass >= 0 && bClass >= 0 && aClass =!= bClass)
      if (aClass > bClass) Some(a) else Some(b)
    else None
  }

  /**
   * Smushes opposing brackets ("[]" or "]["), braces ("{}" or "}{") and parentheses ("()" or ")(") together, replacing
   * any such pair with a vertical bar ("|").
   */
  private def oppositePairSmushingRule: SmushingStrategy = {
    case ('{', '}') => Some('|')
    case ('}', '{') => Some('|')
    case ('[', ']') => Some('|')
    case (']', '[') => Some('|')
    case ('(', ')') => Some('|')
    case (')', '(') => Some('|')
    case _          => None
  }

  /**
   * Smushes "/\" into "|", "\/" into "Y", and "><" into "X". Note that "<>" is not smushed in any way by this rule. The
   * name "BIG X" is historical; originally all three pairs were smushed into "X".
   */
  private def bigXSmushingRule: SmushingStrategy = {
    case ('/', '\\') => Some('|')
    case ('\\', '/') => Some('Y')
    case ('>', '<')  => Some('X')
    case _           => None
  }

  /**
   * Smushes two hardblanks together, replacing them with a single hardblank.
   */
  private def hardblankSmushingRule(hardblank: Char): SmushingStrategy = { (a, b) =>
    if (a === hardblank && a === b) Some(a) else None
  }
}
