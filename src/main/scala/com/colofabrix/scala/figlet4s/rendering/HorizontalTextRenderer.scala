package com.colofabrix.scala.figlet4s.rendering

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.rendering.MergeAction._
import scala.annotation.tailrec
import com.colofabrix.scala.figlet4s.options.RenderOptions

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
    val chosenLayout  = options.horizontalLayout
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
    case UniversalHorizontalSmushingLayout         => universalHorizontalSmushingStrategy(hardblank)
    case ControlledHorizontalSmushingLayout(rules) => controlledHorizontalSmushingStrategy(rules, hardblank)
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
      case (aChar, ' ')                      => Continue(aChar)
      case (' ', bChar)                      => Continue(bChar)
      case (aChar, _) if aChar === hardblank => Stop
      case (_, bChar) if bChar === hardblank => Stop
      case (_, _)                            => Stop
    }

  /**
   * Encodes the Universal Horizontal Smushing horizontal layout
   */
  private def universalHorizontalSmushingStrategy(hardblank: Char): MergeStrategy =
    mergeColumnWith {
      case (aChar, ' ')                      => Continue(aChar)
      case (' ', bChar)                      => Continue(bChar)
      case (aChar, _) if aChar === hardblank => Stop
      case (_, bChar) if bChar === hardblank => Stop
      case (_, bChar)                        => CurrentLast(bChar)
    }

  /**
   * Encodes the Controlled Horizontal Smushing horizontal layout
   */
  private def controlledHorizontalSmushingStrategy(
      rules: Vector[HorizontalSmushingRule],
      hardblank: Char,
  ): MergeStrategy =
    mergeColumnWith {
      case (aChar, ' ')                          => Continue(aChar)
      case (' ', bChar)                          => Continue(bChar)
      case (aChar, bChar) if aChar === hardblank => CurrentLast(bChar)
      case (aChar, bChar) if bChar === hardblank => CurrentLast(aChar)
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
  Explanation of the general algorithm with final `overlap = 3`


  Example merged FIGures (using Horizontal Fitting as example renderer):

     FIGure A   FIGure B        Resulting FIGure
    /        \ /       \       /               \
    +-----+---+---+-----+      +-----+---+-----+
    |  ___|__ |   |     |      |  ___|__ |     |
    | |  _|__||   |__ _ |      | |  _|__||__ _ |
    | | |_|   |  /| _` ||  ->  | | |_|  /| _` ||
    | |  _||  | | |(_| ||  ->  | |  _||| |(_| ||
    | |_| |   |  \|__,_||      | |_| |  \|__,_||
    |     |   |   |     |      |     |   |     |
    +-----+---+---+-----+      +-----+---+-----+
           \     /                     |
        Overlap area                 Merged


  In this example each FIGure is broken down in SubColumns with final `overlap = 3`:

  FIGure A                                          | A-overlapping |
  +--------+           +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
  |  _____ |           | |   | |   |_|   |_|   |_|   |_|   |_|   | |
  | |  ___||           | |   |||   | |   | |   |_|   |_|   |_|   |||
  | | |_   |       ->  | | + ||| + | | + ||| + |_| + | | + | | + | |
  | |  _|  |       ->  | | + ||| + | | + | | + |_| + ||| + | | + | |
  | |_|    |           | |   |||   |_|   |||   | |   | |   | |   | |
  |        |           | |   | |   | |   | |   | |  /| |   | |   | |
  +--------+           +-+   +-+   +-+   +-+   +-+ / +-+   +-+   +-+
                                                  /  |             |
                                  A active column    |             |-- Final overlap = 3 columns
  Resulting FIGure                                   |             |
  +-------------+      +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
  |  _____      |      | |   | |   |_|   |_|   |_|   |_|   |_|   | |   | |   | |   | |   | |   | |
  | |  ___|__ _ |      | |   |||   | |   | |   |_|   |_|   |_|   |||   |_|   |_|   | |   |_|   | |
  | | |_  / _` ||  ->  | | + ||| + | | + ||| + |_| + | | + | | + |/| + | | + |_| + |`| + | | + |||
  | |  _|| (_| ||  ->  | | + ||| + | | + | | + |_| + ||| + ||| + | | + |(| + |_| + ||| + | | + |||
  | |_|   \__,_||      | |   |||   |_|   |||   | |   | |   | |   |\|   |_|   |_|   |,|   |_|   |||
  |             |      | |   | |   | |   | |   | |   | |   | |   | |   | |   | |   | |   | |   | |
  +-------------+      +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
                                                     |             |
                                  B active column    |             |
  FIGure B                                        \  |             |
  +--------+                                       \ +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
  |        |                                        \| |   | |   | |   | |   | |   | |   | |   | |
  |   __ _ |                                         | |   | |   | |   |_|   |_|   | |   |_|   | |
  |  / _` ||      ->                                 | | + | | + |/| + | | + |_| + |`| + | | + |||
  | | (_| ||      ->                                 | | + ||| + | | + |(| + |_| + ||| + | | + |||
  |  \__,_||                                         | |   | |   |\|   |_|   |_|   |,|   |_|   |||
  |        |                                         | |   | |   | |   | |   | |   | |   | |   | |
  +--------+                                         +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
                                                    | B-overlapping |


  Merge of a single overlapping column with the custom merge function `f`:

  +-+     +-+                                                 +-+
  |_|  +  | |  ->  f('_', ' ') = Continue('_')                |_|
  |_|  +  | |  ->  f('_', ' ') = Continue('_')                |_|
  | |  +  | |  ->  f(' ', ' ') = Continue(' ')  ->  Continue( | | )
  | |  +  |||  ->  f(' ', '|') = Continue('|')                |||
  | |  +  | |  ->  f(' ', ' ') = Continue(' ')                | |
  | |  +  | |  ->  f(' ', ' ') = Continue('_')                |_|
  +-+     +-+                                                 +-+


  NOTES:
  - Each recursive iteration works with a certain number of overlapping columns and once the overlapping area has been
    processed it decides between 3 options:
    - the overlap of the current iteration results in a valid merge the overlap can be increased further and thus runs a
      new iteration with `overlap + 1`;
    - the overlap of the current iteration results in a valid merge but the overlap cannot be increased and returns the
      result of the current iteration as the final result;
    - the overlap of the current iteration does not results in a valid merge and the result of the previous iteration is
      used as the final result.
  - At `overlap = n` the `n - 1` overlap values have already passed through the merge algorithm and their result is
    assumed to be a valid merge.
  - The "A active column" and the "B active column" (see figures above) are the columns that decide the result of the
    iteration.
  - Each pair of corresponding characters of the active columns are passed to a custom merge function.
  - The custom merge function returns the character resulting from merge of the two corresponding character together
    with the decision of how to proceed with the algorithm.
  - The result value of the custom merge function is an Applicative Functor.
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
    else if (overlap === 0)
      merge(a, b, 1, a ++ b)(f)
    else if (overlap >= a.length || overlap >= b.length)
      partial
    else {
      // Split the columns into left, right, A-overlapping and B-overlapping
      val (left, aOverlap)  = a.splitAt(a.length - overlap)
      val (bOverlap, right) = b.splitAt(overlap)

      // For each overlapping column apply the merge function to the matching pairs of characters
      val mergedOverlapping =
        (aOverlap zip bOverlap)
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
