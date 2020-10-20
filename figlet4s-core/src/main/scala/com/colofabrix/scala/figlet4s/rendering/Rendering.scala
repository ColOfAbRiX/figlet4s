package com.colofabrix.scala.figlet4s.rendering

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.rendering.MergeAction._
import scala.annotation.tailrec

/**
 * Rendering functions
 *
 * Explanation of the general algorithm with final `overlap = 3` and print direction left-to-right
 *
 * Example merged FIGures (using Horizontal Fitting as example renderer):
 *
 *    FIGure A   FIGure B        Resulting FIGure
 *   /        \ /       \       /               \
 *   +-----+---+---+-----+      +-----+---+-----+
 *   |  ___|__ |   |     |      |  ___|__ |     |
 *   | |  _|__||   |__ _ |      | |  _|__||__ _ |
 *   | | |_|   |  /| _` ||  ->  | | |_|  /| _` ||
 *   | |  _||  | | |(_| ||  ->  | |  _||| |(_| ||
 *   | |_| |   |  \|__,_||      | |_| |  \|__,_||
 *   |     |   |   |     |      |     |   |     |
 *   +-----+---+---+-----+      +-----+---+-----+
 *          \     /                     |
 *       Overlap area                 Merged
 *
 * In this example each FIGure is broken down in SubColumns with final `overlap = 3`:
 *
 * FIGure A                                          | A-overlapping |
 * +--------+           +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
 * |  _____ |           | |   | |   |_|   |_|   |_|   |_|   |_|   | |
 * | |  ___||           | |   |||   | |   | |   |_|   |_|   |_|   |||
 * | | |_   |       ->  | | + ||| + | | + ||| + |_| + | | + | | + | |
 * | |  _|  |       ->  | | + ||| + | | + | | + |_| + ||| + | | + | |
 * | |_|    |           | |   |||   |_|   |||   | |   | |   | |   | |
 * |        |           | |   | |   | |   | |   | |  /| |   | |   | |
 * +--------+           +-+   +-+   +-+   +-+   +-+ / +-+   +-+   +-+
 *                                                 /  |             |
 *                                 A active column    |             |-- Final overlap = 3 columns
 * Resulting FIGure                                   |             |
 * +-------------+      +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
 * |  _____      |      | |   | |   |_|   |_|   |_|   |_|   |_|   | |   | |   | |   | |   | |   | |
 * | |  ___|__ _ |      | |   |||   | |   | |   |_|   |_|   |_|   |||   |_|   |_|   | |   |_|   | |
 * | | |_  / _` ||  ->  | | + ||| + | | + ||| + |_| + | | + | | + |/| + | | + |_| + |`| + | | + |||
 * | |  _|| (_| ||  ->  | | + ||| + | | + | | + |_| + ||| + ||| + | | + |(| + |_| + ||| + | | + |||
 * | |_|   \__,_||      | |   |||   |_|   |||   | |   | |   | |   |\|   |_|   |_|   |,|   |_|   |||
 * |             |      | |   | |   | |   | |   | |   | |   | |   | |   | |   | |   | |   | |   | |
 * +-------------+      +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
 *                                                    |             |
 *                                 B active column    |             |
 * FIGure B                                        \  |             |
 * +--------+                                       \ +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
 * |        |                                        \| |   | |   | |   | |   | |   | |   | |   | |
 * |   __ _ |                                         | |   | |   | |   |_|   |_|   | |   |_|   | |
 * |  / _` ||      ->                                 | | + | | + |/| + | | + |_| + |`| + | | + |||
 * | | (_| ||      ->                                 | | + ||| + | | + |(| + |_| + ||| + | | + |||
 * |  \__,_||                                         | |   | |   |\|   |_|   |_|   |,|   |_|   |||
 * |        |                                         | |   | |   | |   | |   | |   | |   | |   | |
 * +--------+                                         +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
 *                                                   | B-overlapping |
 *
 * Merge of a single overlapping column with the custom merge function `f`:
 *
 * +-+     +-+                                                 +-+
 * |_|  +  | |  ->  f('_', ' ') = Continue('_')                |_|
 * |_|  +  | |  ->  f('_', ' ') = Continue('_')                |_|
 * | |  +  | |  ->  f(' ', ' ') = Continue(' ')  ->  Continue( | | )
 * | |  +  |||  ->  f(' ', '|') = Continue('|')                |||
 * | |  +  | |  ->  f(' ', ' ') = Continue(' ')                | |
 * | |  +  | |  ->  f(' ', ' ') = Continue('_')                |_|
 * +-+     +-+                                                 +-+
 *
 * NOTES:
 * - Each recursive iteration works with a certain number of overlapping columns and once the overlapping area has been
 *   processed it decides between 3 options:
 *   - the overlap of the current iteration results in a valid merge the overlap can be increased further and thus runs
 *     a new iteration with `overlap + 1`;
 *   - the overlap of the current iteration results in a valid merge but the overlap cannot be increased and returns the
 *     result of the current iteration as the final result;
 *   - the overlap of the current iteration does not results in a valid merge and the result of the previous iteration
 *     is used as the final result.
 * - At `overlap = n` the `n - 1` overlap values have already passed through the merge algorithm and their result is
 *   assumed to be a valid merge.
 * - The "A active column" and the "B active column" (see figures above) are the columns that decide the result of the
 *   iteration.
 * - Each pair of corresponding characters of the active columns are passed to a custom merge function.
 * - The custom merge function returns the character resulting from merge of the two corresponding character together
 *   with the decision of how to proceed with the algorithm.
 * - The result value of the custom merge function is an Applicative Functor.
 */
private[figlet4s] object Rendering {
  /** Function that merges two SubElements */
  type MergeStrategy = (SubColumns, SubColumns) => SubColumns

  /** Function that smushes two characters */
  type SmushingStrategy = (Char, Char) => Option[Char]

  /** Options carried around on each merge operation */
  case class MergeOptions(hardblank: Char)

  /** Function that merges two characters */
  private type MergeChars = (Char, Char) => MergeAction[Char]

  /** Shortcut for a set of columns */
  private type Columns = Vector[String]

  /** Type of the foldLeft function */
  private type Folder = (FoldAccumulator, SubColumns) => FoldAccumulator

  /**
   * The data structure used to carry along information on each fold iteration
   */
  private case class FoldAccumulator(accumulator: Vector[SubColumns], lastCharWidth: Int)
  private object FoldAccumulator {
    def apply(font: FIGfont): FoldAccumulator = FoldAccumulator(Vector(font.zero.lines.toSubcolumns), 0)
  }

  /**
   * Represents the three sections of a set of columns
   */
  private case class Sections(left: Columns, overlap: Columns, right: Columns)
  private object Sections {
    def apply(): Sections = Sections(Vector.empty[String], Vector.empty[String], Vector.empty[String])
  }

  //  Interface  //

  /**
   * Renders a text horizontally by merging one character after the other
   */
  def horizontalRender(text: String, options: RenderOptions, mergeStrategy: MergeStrategy): FIGure = {
    val result = text
      .map(options.font(_).columns)
      .foldLeft(FoldAccumulator(options.font))(appendCharacter(options, mergeStrategy))
    FIGure(options.font, text, result.accumulator)
  }

  /**
   * Merges two columns applying a custom merge function to each pair of character of the two columns
   */
  def mergeColumnWith(options: MergeOptions)(f: MergeChars): MergeStrategy = { (a, b) =>
    SubColumns(merge(options, a.value.toVector, b.value.toVector, 0, Vector.empty)(f))
  }

  //  Support  //

  /**
   * Appends a new character to the accumulator or creates a new line for it
   */
  private def appendCharacter(options: RenderOptions, mergeStrategy: MergeStrategy): Folder = {
    case (FoldAccumulator(accumulator, lastCharWidth), column) =>
      cats.effect.IO(lastCharWidth).map(_ => ()).unsafeRunSync()
      val result = accumulator
        .lastOption
        .map(lastLine => accumulator.drop(1) :+ mergeStrategy(lastLine, column))
        .filter(_.length <= options.maxWidth)
        .getOrElse(accumulator :+ column)
      FoldAccumulator(result, column.length)
  }

  @tailrec
  private def merge(opts: MergeOptions, a: Columns, b: Columns, overlap: Int, previous: Columns)(f: MergeChars): Columns =
    if (overlap === 0) {
      merge(opts, a, b, 1, a ++ b)(f)

    } else if (overlap > b.length) {
      previous

    } else {
      val aLeftCut  = Math.max(0, a.length - overlap)
      val aRightCut = Math.min(a.length, (a.length - overlap) + b.length)
      val aSections = splitSections(aLeftCut, aRightCut, a)

      val bLeftCut  = Math.max(0, overlap - a.length)
      val bRightCut = Math.min(overlap, b.length)
      val bSections = splitSections(bLeftCut, bRightCut, b)

      val leftSide  = mergeOnLeftBorder(opts, bSections.left, f)
      val merged    = mergeOverlappingSections(aSections.overlap, bSections.overlap, f)
      val rightSide = Continue(aSections.right ++ bSections.right)

      val result = (leftSide, merged, rightSide).mapN { (_, merged, right) =>
        aSections.left ++ merged ++ right
      }

      result match {
        case Stop                 => previous
        case CurrentLast(current) => current
        case Continue(value)      => merge(opts, a, b, overlap + 1, value)(f)
      }
    }

  private def mergeOverlappingSections(aSection: Columns, bSection: Columns, f: MergeChars): MergeAction[Columns] =
    (aSection zip bSection)
      .traverse {
        case (aActiveColumn, bActiveColumn) =>
          (aActiveColumn zip bActiveColumn)
            .toVector
            .traverse(f.tupled)
            .map(_.mkString)
      }

  private def mergeOnLeftBorder(opts: MergeOptions, section: Columns, f: MergeChars): MergeAction[Unit] =
    section
      .traverse {
        _.toVector.traverse {
          case ' ' => f(opts.hardblank, ' ')
          case _   => Stop
        }
      }.map(_ => ())

  private def splitSections(aPoint: Int, bPoint: Int, figure: Columns): Sections =
    figure
      .zipWithIndex
      .foldLeft(Sections()) {
        case (store, (column, i)) =>
          if (i < aPoint)
            store.copy(left = store.left :+ column)
          else if (i >= bPoint)
            store.copy(right = store.right :+ column)
          else
            store.copy(overlap = store.overlap :+ column)
      }
}
