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
    val font          = options.font
    val empty         = font.zero.lines.toSubcolumns
    val chosenLayout  = ClientHorizontalLayout.toInternalLayout(font)(options.horizontalLayout)
    val mergeStrategy = layout2mergeStrategy(font.header.hardblank)(chosenLayout)

    val result = text
      .map(font(_).columns)
      .foldLeft(Vector(empty))(appendCharacter(options, mergeStrategy))

    FIGure(font, text, result)
  }

  /**
   * Appends a new character to the accumulator or creates a new line for it
   */
  private def appendCharacter(
      options: RenderOptions,
      merge: MergeStrategy,
  )(accumulator: Vector[SubColumns], column: SubColumns): Vector[SubColumns] =
    accumulator
      .lastOption
      .map(lastLine => accumulator.drop(1) :+ merge(lastLine, column))
      .filter(_.length <= options.maxWidth)
      .getOrElse(accumulator :+ column)

  /**
   * Returns the merge strategy function given a layout
   */
  private def layout2mergeStrategy(hardblank: Char): PartialFunction[HorizontalLayout, MergeStrategy] = {
    case HorizontalLayout.FullWidthHorizontalLayout         => fullWidthStrategy
    case HorizontalLayout.HorizontalFittingLayout           => horizontalFittingStrategy
    case HorizontalLayout.UniversalHorizontalSmushingLayout => universalHorizontalSmushingStrategy(hardblank)
    case HorizontalLayout.ControlledHorizontalSmushingLayout(rules) =>
      controlledHorizontalSmushingStrategy(rules, hardblank)
  }

  /**
   * Encodes the Full Width horizontal layout
   */
  private def fullWidthStrategy: MergeStrategy =
    mergeColumnWith {
      case (_, _) => Stop
    }

  /**
   * Encodes the Horizontal Fitting horizontal layout
   */
  private def horizontalFittingStrategy: MergeStrategy =
    mergeColumnWith {
      case (aChar, ' ') => Continue(aChar)
      case (' ', bChar) => Continue(bChar)
      case (_, _)       => Stop
    }

  /**
   * Encodes the Universal Horizontal Smushing horizontal layout
   */
  private def universalHorizontalSmushingStrategy(hardblank: Char): MergeStrategy =
    mergeColumnWith {
      case (aChar, ' ') => Continue(aChar)
      case (' ', bChar) => Continue(bChar)
      case (aChar, bChar) if bChar === hardblank =>
        if (aChar === hardblank) Stop else CurrentLast(aChar)
      case (_, bChar) => CurrentLast(bChar)
    }

  /**
   * Encodes the Controlled Horizontal Smushing horizontal layout
   */
  private def controlledHorizontalSmushingStrategy(
      rules: Vector[HorizontalSmushingRule],
      hardblank: Char,
  ): MergeStrategy =
    mergeColumnWith {
      case (aChar, ' ') => Continue(aChar)
      case (' ', bChar) => Continue(bChar)
      case (aChar, bChar) =>
        rules
          .map(rule2smushingStrategy(hardblank))
          .map(f => f(aChar, bChar))
          .collectFirst {
            case Some(value) => CurrentLast(value)
          }
          .getOrElse {
            if (aChar === hardblank || bChar === hardblank) Stop
            else CurrentLast(bChar)
          }
    }

  /**
   * Returns a smushing strategy function given the smushing rule
   */
  private def rule2smushingStrategy(hardblank: Char): PartialFunction[HorizontalSmushingRule, SmushingStrategy] = {
    case HorizontalSmushingRule.EqualCharacterHorizontalSmushing => equalCharacterSmushingRule(hardblank)
    case HorizontalSmushingRule.UnderscoreHorizontalSmushing     => underscoreSmushingRule
    case HorizontalSmushingRule.HierarchyHorizontalSmushing      => hierarchySmushingRule
    case HorizontalSmushingRule.OppositePairHorizontalSmushing   => oppositePairSmushingRule
    case HorizontalSmushingRule.BigXHorizontalSmushing           => bigXSmushingRule
    case HorizontalSmushingRule.HardblankHorizontalSmushing      => hardblankSmushingRule(hardblank)
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
