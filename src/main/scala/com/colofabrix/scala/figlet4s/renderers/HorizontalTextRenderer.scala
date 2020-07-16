package com.colofabrix.scala.figlet4s.renderers

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.renderers.MergeAction
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
  def render(text: String, font: FIGfont, options: RenderOptions = RenderOptions()): FIGure = {
    val empty         = font.zero.lines.toSubcolumns
    val chosenLayout  = options.layout.getOrElse(font.hLayout)
    val mergeStrategy = layout2mergeStrategy(font)(chosenLayout)

    val result = text
      .map(font(_).columns)
      .foldLeft(Vector(empty))(mergeStep(options, mergeStrategy))

    FIGure(font, text, result)
  }

  /**
   * Returns the merge strategy function given a layout
   */
  private def layout2mergeStrategy(font: FIGfont): PartialFunction[HorizontalLayout, MergeStrategy] = {
    case FullWidthHorizontalLayout                 => fullWidthStrategy
    case HorizontalFittingLayout                   => horizontalFittingStrategy
    case UniversalHorizontalSmushingLayout         => controlledHorizontalSmushingStrategy
    case ControlledHorizontalSmushingLayout(rules) => universalHorizontalSmushingStrategy(rules, font)
  }

  private def mergeStep(
      options: RenderOptions,
      merge: MergeStrategy,
  )(accumulator: Vector[SubColumns], column: SubColumns): Vector[SubColumns] =
    accumulator
      .lastOption
      .map { lastLine =>
        accumulator.drop(1) :+ merge(lastLine, column)
      }
      .filter(_.length <= options.maxWidth.getOrElse(Int.MaxValue))
      .getOrElse(accumulator :+ column)

  /**
   * Encodes the Full Width horizontal layout
   */
  private def fullWidthStrategy: MergeStrategy = { (first, second) =>
    merge(first, second) {
      case (_, _) => Stop
    }
  }

  /**
   * Encodes the Horizontal Fitting horizontal layout
   */
  private def horizontalFittingStrategy: MergeStrategy = { (first, second) =>
    merge(first, second) {
      case (' ', ' ')   => Continue(' ')
      case (aChar, ' ') => Continue(aChar)
      case (' ', bChar) => Continue(bChar)
      case (_, _)       => Stop
    }
  }

  /**
   * Encodes the Controlled Horizontal Smushing horizontal layout
   */
  private def controlledHorizontalSmushingStrategy: MergeStrategy = { (first, second) =>
    merge(first, second) {
      case (' ', ' ')   => Continue(' ')
      case (aChar, ' ') => Continue(aChar)
      case (' ', bChar) => Continue(bChar)
      case (_, bChar)   => CurrentLast(bChar)
    }
  }

  /**
   * Encodes the Universal Horizontal Smushing horizontal layout
   */
  private def universalHorizontalSmushingStrategy(
      rules: Vector[HorizontalSmushingRules],
      font: FIGfont,
  ): MergeStrategy = { (first, second) =>
    merge(first, second) {
      case (' ', ' ')     => Continue(' ')
      case (aChar, ' ')   => Continue(aChar)
      case (' ', bChar)   => Continue(bChar)
      case (aChar, bChar) =>
        rules
          .map(rule2smushingStrategy(font))
          .map(f => f(aChar, bChar))
          .collectFirst {
            case Some(value) => CurrentLast(value)
          }
          .getOrElse(CurrentLast(bChar))
    }
  }

  /**
   * Returns a smushing strategy function given the smushing rule
   */
  private def rule2smushingStrategy(font: FIGfont): PartialFunction[HorizontalSmushingRules, SmushingStrategy] = {
    case EqualCharacterHorizontalSmushing => equalCharacterSmushingRule(font.header.hardblank.charAt(0))
    case UnderscoreHorizontalSmushing     => underscoreSmushingRule
    case HierarchyHorizontalSmushing      => hierarchySmushingRule
    case OppositePairHorizontalSmushing   => oppositePairSmushingRule
    case BigXHorizontalSmushing           => bigXSmushingRule
    case HardblankHorizontalSmushing      => hardblankSmushingRule(font.header.hardblank.charAt(0))
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
    val replace = Vector('|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>')
    (a, b) match {
      case ('_', _) if replace.contains(b) => Some(b)
      case (_, '_') if replace.contains(a) => Some(a)
      case _                               => None
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

  private def merge(a: SubColumns, b: SubColumns)(f: (Char, Char) => MergeAction[Char]): SubColumns =
    SubColumns(merge(a.value, b.value, 0, Vector.empty)(f))

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
