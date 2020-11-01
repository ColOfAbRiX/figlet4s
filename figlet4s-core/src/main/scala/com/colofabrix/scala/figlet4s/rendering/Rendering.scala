package com.colofabrix.scala.figlet4s.rendering

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.rendering.MergeAction._
import scala.annotation.tailrec

/**
 * Rendering functions
 *
 * Explanation of the general algorithm with final `overlap = 3`, horizontal fitting layout and print direction
 * left-to-right.
 *
 * Example merged FIGures (using Horizontal Fitting as example renderer):
 *
 * {{{
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
 *       Overlap area            Merged 3 columns
 *       3 cols each
 * }}}
 *
 * In this example each FIGure is broken down in SubColumns with final `overlap = 3`:
 *
 * {{{
 * FIGure A                                          | A-overlapping |
 * +--------+           +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
 * |  _____ |           | |   | |   |_|   |_|   |_|   |_|   |_|   | |
 * | |  ___||           | |   |||   | |   | |   |_|   |_|   |_|   |||
 * | | |_   |       ->  | | + ||| + | | + ||| + |_| + | | + | | + | |
 * | |  _|  |       ->  | | + ||| + | | + | | + |_| + ||| + | | + | |
 * | |_|    |           | |   |||   |_|   |||   | |   | |   | |   | |
 * |        |           | |   | |   | |   | |   | |  /| |   | |   | |
 * +--------+           +-+   +-+   +-+   +-+   +-+ / +-+   +-+   +-+
 *                                                 / |               |
 *                                 A active column   |               |-- Final overlap = 3 columns
 * Resulting FIGure                                  |               |
 * +-------------+      +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
 * |  _____      |      | |   | |   |_|   |_|   |_|   |_|   |_|   | |   | |   | |   | |   | |   | |
 * | |  ___|__ _ |      | |   |||   | |   | |   |_|   |_|   |_|   |||   |_|   |_|   | |   |_|   | |
 * | | |_  / _` ||  ->  | | + ||| + | | + ||| + |_| + | | + | | + |/| + | | + |_| + |`| + | | + |||
 * | |  _|| (_| ||  ->  | | + ||| + | | + | | + |_| + ||| + ||| + | | + |(| + |_| + ||| + | | + |||
 * | |_|   \__,_||      | |   |||   |_|   |||   | |   | |   | |   |\|   |_|   |_|   |,|   |_|   |||
 * |             |      | |   | |   | |   | |   | |   | |   | |   | |   | |   | |   | |   | |   | |
 * +-------------+      +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
 *                                                   |              |
 *                                 B active column   |              |
 * FIGure B                                        \ |              |
 * +--------+                                       \ +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
 * |        |                                        \| |   | |   | |   | |   | |   | |   | |   | |
 * |   __ _ |                                         | |   | |   | |   |_|   |_|   | |   |_|   | |
 * |  / _` ||      ->                                 | | + | | + |/| + | | + |_| + |`| + | | + |||
 * | | (_| ||      ->                                 | | + ||| + | | + |(| + |_| + ||| + | | + |||
 * |  \__,_||                                         | |   | |   |\|   |_|   |_|   |,|   |_|   |||
 * |        |                                         | |   | |   | |   | |   | |   | |   | |   | |
 * +--------+                                         +-+   +-+   +-+   +-+   +-+   +-+   +-+   +-+
 *                                                   | B-overlapping |
 * }}}
 *
 * Merge of a single overlapping column with the custom merge function `f`:
 *
 * {{{
 * +-+     +-+                                                 +-+
 * |_|  +  | |  ->  f('_', ' ') = Continue('_')                |_|
 * |_|  +  | |  ->  f('_', ' ') = Continue('_')                |_|
 * | |  +  | |  ->  f(' ', ' ') = Continue(' ')  ->  Continue( | | )
 * | |  +  |||  ->  f(' ', '|') = Continue('|')                |||
 * | |  +  | |  ->  f(' ', ' ') = Continue(' ')                | |
 * | |  +  | |  ->  f(' ', ' ') = Continue('_')                |_|
 * +-+     +-+                                                 +-+
 * }}}
 *
 * NOTES:
 * - Each recursive iteration works with a certain amount of overlapping columns. The entire overlapping area is merged
 *   but it's only the "active columns" that decide the outcome of the iteration as the subsequent ones will merge for
 *   sure. Once the overlapping area has been process it decides between 3 options:
 *   - the overlap of the current iteration results in a valid merge, the overlap can be increased further and thus runs
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

  /** Function that merges two characters */
  type MergeStrategy = (Char, Char) => MergeAction[Char]

  /** Function that smushes two characters */
  type SmushingStrategy = (Char, Char) => Option[Char]

  /**
   * Renders a text horizontally by merging one character after the other
   *
   * @param text          The text to render
   * @param options       The options of the rendering
   * @param mergeStrategy The merge strategy used to merge together two characters
   * @return A new FIGure that corresponds to the rendered text
   */
  def horizontalRender(text: String, options: RenderOptions, mergeStrategy: MergeStrategy): FIGure = {
    val font = options.font
    val zero = Vector(font.zero.lines.toSubcolumns)

    val result =
      text
        .map(options.font(_).columns)
        .foldLeft((zero, 0)) {
          case ((accumulator, lastCharWidth), currentChar) =>
            val nextAccumulator = accumulator
              .lastOption
              .map { lastLine =>
                val state =
                  MergeState(lastLine.value.toVector, currentChar.value.toVector, 0, Vector.empty, lastCharWidth)
                accumulator.drop(1) :+ SubColumns(merge(options, state, mergeStrategy))
              }
              .filter(_.length <= options.maxWidth)
              .getOrElse(accumulator :+ currentChar)
            (nextAccumulator, currentChar.length)
        }

    FIGure(font, text, result._1)
  }

  //  Support  //

  /** Shortcut for a set of columns */
  private type Columns = Vector[String]

  /** Status of the merge loop */
  final private case class MergeState(a: Columns, b: Columns, overlap: Int, partialResult: Columns, lastCharWidth: Int)

  /** Represents the three sections of a set of columns */
  final private case class Sections(left: Columns, overlap: Columns, right: Columns)

  def printState(state: MergeState, result: Object): Unit = {
    def indent(text: Object, amount: Int): String = text.toString.replaceAll("(?m)^", " " * amount)
    println("-" * 50)
    println(s"Overlap: ${state.overlap}")
    println(s"Last char width: ${state.lastCharWidth}")
    println("  A")
    println(s"    a.length: ${state.a.length}")
    println(indent(SubColumns(state.a), 4))
    println("  B")
    println(s"    b.length: ${state.b.length}")
    println(indent(SubColumns(state.b), 4))
    println("  RESULT")
    println(s"    $result")
  }

  @tailrec
  private def merge(options: RenderOptions, state: MergeState, f: MergeStrategy): Columns =
    if (state.overlap === 0) {
      // printState(state, "NOTHING DO TO, GO FOR OVERLAPPING = 1")
      merge(options, state.copy(overlap = 1, partialResult = state.a ++ state.b), f)

    } else if (state.overlap > state.b.length || smushingException(options, state)) {
      // printState(state, "RETURNING THE PREVIOUS RESULT")
      state.partialResult

    } else {
      val aLeftCut  = Math.max(0, state.a.length - state.overlap)
      val aRightCut = Math.min(state.a.length, (state.a.length - state.overlap) + state.b.length)
      val aSections = splitSections(aLeftCut, aRightCut, state.a)

      val bLeftCut  = Math.max(0, state.overlap - state.a.length)
      val bRightCut = Math.min(state.overlap, state.b.length)
      val bSections = splitSections(bLeftCut, bRightCut, state.b)

      val leftSide  = mergeOnLeftBorder(options.font.header.hardblank, bSections.left, f)
      val merged    = mergeOverlappingSections(aSections.overlap, bSections.overlap, f)
      val rightSide = Continue(aSections.right ++ bSections.right)

      val result = (leftSide, merged, rightSide).mapN { (_, merged, right) =>
        aSections.left ++ merged ++ right
      }

      // printState(state, result.map(SubColumns(_)))

      result match {
        case Stop =>
          // println("=" * 50 + "\n")
          state.partialResult
        case CurrentLast(current) =>
          // println("=" * 50 + "\n")
          current
        case Continue(value) =>
          merge(options, state.copy(overlap = state.overlap + 1, partialResult = value), f)
      }
    }

  /**
   * A note in the original figlet source code states: "Disallows overlapping if the previous character or the current
   * character has a width of 1 or zero". This is an undocumented behaviour.
   */
  private def smushingException(options: RenderOptions, state: MergeState): Boolean =
    options.horizontalLayout match {
      case HorizontalLayout.HorizontalSmushing | HorizontalLayout.ForceHorizontalSmushing =>
        state.lastCharWidth <= 1 || state.b.length <= 1
      case _ =>
        false
    }

  private def mergeOverlappingSections(aSection: Columns, bSection: Columns, f: MergeStrategy): MergeAction[Columns] =
    (aSection zip bSection)
      .traverse {
        case (aActiveColumn, bActiveColumn) =>
          (aActiveColumn zip bActiveColumn)
            .toVector
            .traverse(f.tupled)
            .map(_.mkString)
      }

  private def mergeOnLeftBorder(hardblank: Char, section: Columns, f: MergeStrategy): MergeAction[Unit] =
    section
      .traverse {
        _.toVector.traverse {
          case ' ' => f(hardblank, ' ')
          case _   => Stop
        }
      }.map(_ => ())

  private def splitSections(aPoint: Int, bPoint: Int, figure: Columns): Sections =
    figure
      .zipWithIndex
      .foldLeft(Sections(Vector.empty[String], Vector.empty[String], Vector.empty[String])) {
        case (store, (column, i)) =>
          if (i < aPoint)
            store.copy(left = store.left :+ column)
          else if (i >= bPoint)
            store.copy(right = store.right :+ column)
          else
            store.copy(overlap = store.overlap :+ column)
      }
}
