package com.colofabrix.scala.figlet4s.renderers

import cats.implicits._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.renderers._
import com.colofabrix.scala.figlet4s.renderers.MergeAction._
import scala.annotation.tailrec

/**
 * Renderer for Horizontal Layouts
 */
object HorizontalTextRenderer {
  private type MergeStrategy    = (SubColumns, SubColumns) => SubColumns
  private type SmushingStrategy = (Char, Char) => Option[Char]

  /**
   * Renders a String into a FIGure for a given FIGfont and options
   */
  def render(text: String, options: RenderOptions): FIGure = {
    val font          = options.font
    val empty         = font.zero.lines.toSubcolumns
    val chosenLayout  = options.layout.getOrElse(font.hLayout)
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
    case FullWidthHorizontalLayout                 => fullWidthStrategy
    case HorizontalFittingLayout                   => horizontalFittingStrategy(hardblank)
    case UniversalHorizontalSmushingLayout         => controlledHorizontalSmushingStrategy(hardblank)
    case ControlledHorizontalSmushingLayout(rules) => universalHorizontalSmushingStrategy(rules, hardblank)
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
  private def horizontalFittingStrategy(hardblank: Char): MergeStrategy =
    mergeColumnWith {
      case (' ', ' ')                          => Continue(' ')
      case (a, ' ') if a =!= hardblank         => Continue(a)
      case (' ', bChar) if bChar =!= hardblank => Continue(bChar)
      case (_, _)                              => Stop
    }

  /**
   * Encodes the Controlled Horizontal Smushing horizontal layout
   */
  private def controlledHorizontalSmushingStrategy(hardblank: Char): MergeStrategy =
    mergeColumnWith {
      case (' ', ' ')                          => Continue(' ')
      case (a, ' ') if a =!= hardblank         => Continue(a)
      case (' ', bChar) if bChar =!= hardblank => Continue(bChar)
      case (' ', bChar) if bChar === hardblank => Stop
      case (_, bChar)                          => CurrentLast(bChar)
    }

  /**
   * Encodes the Universal Horizontal Smushing horizontal layout
   */
  private def universalHorizontalSmushingStrategy(rules: Vector[HorizontalSmushingRule], hardblank: Char): MergeStrategy =
    mergeColumnWith {
      case (' ', ' ')                          => Continue(' ')
      case (a, ' ') if a =!= hardblank         => Continue(a)
      case (' ', bChar) if bChar =!= hardblank => Continue(bChar)
      case (' ', bChar) if bChar === hardblank => Stop
      case (a, bChar)                          =>
        rules
          .map(rule2smushingStrategy(hardblank))
          .map(f => f(a, bChar))
          .collectFirst {
            case Some(value) => CurrentLast(value)
          }
          .getOrElse(CurrentLast(bChar))
    }

  /**
   * Returns a smushing strategy function given the smushing rule
   */
  private def rule2smushingStrategy(hardblank: Char): PartialFunction[HorizontalSmushingRule, SmushingStrategy] = {
    case EqualCharacterHorizontalSmushing => equalCharacterSmushingRule(hardblank)
    case UnderscoreHorizontalSmushing     => underscoreSmushingRule
    case HierarchyHorizontalSmushing      => hierarchySmushingRule
    case OppositePairHorizontalSmushing   => oppositePairSmushingRule
    case BigXHorizontalSmushing           => bigXSmushingRule
    case HardblankHorizontalSmushing      => hardblankSmushingRule(hardblank)
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
    if (aClass >= 0 && bClass >= 0 && aClass =!= bClass) Some(b) else None
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

  /*
  Explanation of the general algorithm

  Merged FIGures (using Horizontal Fitting as example renderer):

     FIGure A   FIGure B      Resulting FIGure
    /        \ /       \     /               \
    +-----+---+---+-----+    +-----+---+-----+
    |  ___|__ |   |     |    |  ___|__ |     |
    | |  _|__||   |__ _ |    | |  _|__||__ _ |
    | | |_|   |  /| _` || -> | | |_|  /| _` ||
    | |  _||  | | |(_| || -> | |  _||| |(_| ||
    | |_| |   |  \|__,_||    | |_| |  \|__,_||
    |     |   |   |     |    |     |   |     |
    +-----+---+---+-----+    +-----+---+-----+
          \     /                    |
       Overlap area               Merged

  Each FIGure is broken down in SubColumns:

  FIGure A
  +--------+         +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
  |  _____ |         | |   | |   |_|   |_|   |_|   |_|   |_|   | |
  | |  ___||         | |   |||   | |   | |   |_|   |_|   |_|   |||
  | | |_   |      -> | | + ||| + | | + ||| + |_| + | | + | | + | |
  | |  _|  |      -> | | + ||| + | | + | | + |_| + ||| + | | + | |
  | |_|    |         | |   |||   |_|   |||   | |   | |   | |   | |
  |        |         | |   | |   | |   | |   | |   | |   | |   | |
  +--------+         +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
                                                   |             |
                                                   |             | --- Final overlap = 3 columns
  FIGure B                                         |             |
  +--------+                                       +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
  |        |                                       | |   | |   | |   | |   | |   | |   | |   | |
  |   __ _ |                                       | |   | |   | |   |_|   |_|   | |   |_|   | |
  |  / _` ||      ->                               | | + | | + |/| + | | + |_| + |`| + | | + |||
  | | (_| ||      ->                               | | + ||| + | | + |(| + |_| + ||| + | | + |||
  |  \__,_||                                       | |   | |   |\|   |_|   |_|   |,|   |_|   |||
  |        |                                       | |   | |   | |   | |   | |   | |   | |   | |
  +--------+                                       +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
                                                   |             |
                                                   |             |
  Resulting FIGure                                 |             |
  +-------------+    +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
  |  _____      |    | |   | |   |_|   |_|   |_|   |_|   |_|   | |   | |   | |   | |   | |   | |
  | |  ___|__ _ |    | |   |||   | |   | |   |_|   |_|   |_|   |||   |_|   |_|   | |   |_|   | |
  | | |_  / _` || -> | | + ||| + | | + ||| + |_| + | | + | | + |/| + | | + |_| + |`| + | | + |||
  | |  _|| (_| || -> | | + ||| + | | + | | + |_| + ||| + ||| + | | + |(| + |_| + ||| + | | + |||
  | |_|   \__,_||    | |   |||   |_|   |||   | |   | |   | |   |\|   |_|   |_|   |,|   |_|   |||
  |             |    | |   | |   | |   | |   | |   | |   | |   | |   | |   | |   | |   | |   | |
  +-------------+    +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+

  Merge of overlapping columns with the custom merge function

  +-+     +-+                                +-+
  |_|  +  | |  ->  Continue("_")             |_|
  |_|  +  | |  ->  Continue("_")             |_|
  | |  +  | |  ->  Continue(" ") -> Continue(| |)
  | |  +  |||  ->  Continue("|")             |||
  | |  +  | |  ->  Continue(" ")             | |
  | |  +  | |  ->  Continue("_")             |_|
  +-+     +-+                                +-+

  NOTES:
  - Each recursive call of the algorithm works with one active column from each FIGure at the time.
  - A custom merge strategy function is used to determine how the two active columns are merged.
  - The custom merge function works by accepting corresponding character from the two active column and by returning the
    character resulting from their merger. Together with this the function can instruct the algorithm on how to proceed
    next.
  - Once the two columns have been merged the algorithm decides what to do next:
    - It stores the result of the merge and does a recursive call to merge the next column
    - It stores the result of the merge and stops more processing
    - It uses the result of the merge of the previous recursive call and stops more processing
   */

  private def mergeColumnWith(f: (Char, Char) => MergeAction[Char]): MergeStrategy = { (a, b) =>
    SubColumns(merge(a.value, b.value, 0, Vector.empty)(f))
  }

  @tailrec
  private def merge(a: Vector[String], b: Vector[String], overlap: Int, partial: Vector[String])(
      f: (Char, Char) => MergeAction[Char],
  ): Vector[String] = {
    if (a.length === 0)
      b
    else if (b.length === 0)
      a
    else if (a.length < overlap || b.length < overlap)
      partial
    else if (overlap === 0)
      merge(a, b, 1, a ++ b)(f)
    else {
      // Split the columns into left, right, A-overlapping and B-overlapping
      val (left, aOverlap)  = a.splitAt(a.length - overlap)
      val (bOverlap, right) = b.splitAt(overlap)

      // For each overlapping column apply the merge function to the matching pairs of characters
      val mergedOverlapping =
        (aOverlap zip bOverlap)
          .toVector
          .traverse {
            case (aActiveColumn, bActiveColumn) =>
              (aActiveColumn zip bActiveColumn)
                .toVector
                .traverse(f.tupled)
                .map(_.mkString)
          }
          .map(left ++ _ ++ right)

      // Given the result of the merge, decide how to proceed
      mergedOverlapping match {
        case Stop                 => partial
        case CurrentLast(current) => current
        case Continue(value)      => merge(a, b, overlap + 1, value)(f)
      }
    }
  }
}
