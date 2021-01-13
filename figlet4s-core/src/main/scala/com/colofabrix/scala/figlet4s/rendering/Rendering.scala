package com.colofabrix.scala.figlet4s.rendering

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.rendering.MergeAction._
import com.colofabrix.scala.figlet4s.rendering.Rendering._
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
 *
 * @param text          The text to render
 * @param options       The options of the rendering
 */
final private[rendering] class Rendering(options: RenderOptions) {

  /**
   * Renders a String into a FIGure for a given FIGfont and options
   *
   * @return A FIGure containing the rendered text following the rendering options
   */
  def render(text: String): FIGure = {
    val figures = text.map(options.font(_).columns.value.toVector).toVector
    val zero    = Vector(options.font.zero.lines.toSubcolumns.value.toVector)
    val result  = appendLoop(figures, zero, AppendLoopState()).map(SubColumns(_))
    FIGure(options.font, text, result)
  }

  //  ----  //

  private val mergeStrategy: MergeStrategy = HorizontalMergeRules.mergeStrategy(options)

  @tailrec
  private def appendLoop(figures: Vector[Columns], partial: Vector[Columns], state: AppendLoopState): Vector[Columns] =
    (figures, partial) match {
      case (Vector(), _) => partial
      case (figChar +: remainingChars, upperLines :+ lastLine) =>
        val merged    = merge(MergeLoopState(lastLine, figChar, appendLoopState = state))
        def onBorder  = merge(MergeLoopState(b = figChar))
        val result    = if (merged.length <= options.maxWidth) upperLines :+ merged else partial :+ onBorder
        val nextState = state.copy(lastCharWidth = figChar.length)
        appendLoop(remainingChars, result, nextState)
    }

  //  ----  //

  @tailrec
  private def merge(state: MergeLoopState): Columns =
    if (state.overlap === 0) {
      merge(state.copy(overlap = 1, partialResult = state.a ++ state.b))

    } else if (state.overlap > state.b.length) {
      state.partialResult

    } else {
      val mState = MergeState(state.overlap, state.b.length, state.appendLoopState.lastCharWidth)

      val aLeftCut  = Math.max(0, state.a.length - state.overlap)
      val aRightCut = Math.min(state.a.length, (state.a.length - state.overlap) + state.b.length)
      val aSections = splitSections(aLeftCut, aRightCut, state.a)

      val bLeftCut  = Math.max(0, state.overlap - state.a.length)
      val bRightCut = Math.min(state.overlap, state.b.length)
      val bSections = splitSections(bLeftCut, bRightCut, state.b)

      val leftSide  = mergeOnLeftBorder(mState, bSections.left)
      val merged    = mergeOverlappingSections(mState, aSections.overlap, bSections.overlap)
      val rightSide = Continue(aSections.right ++ bSections.right)

      val result = (leftSide, merged, rightSide).mapN { (_, merged, right) =>
        aSections.left ++ merged ++ right
      }

      result match {
        case Stop                 => state.partialResult
        case CurrentLast(current) => current
        case Continue(value)      => merge(state.copy(overlap = state.overlap + 1, partialResult = value))
      }
    }

  /** Divides a set of columns into 3 sections with cuts in 2 points */
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

  /** Merges the two overlapping sections of two characters */
  private def mergeOverlappingSections(state: MergeState, aSection: Columns, bSection: Columns): MergeAction[Columns] =
    (aSection zip bSection)
      .traverse {
        case (aActiveColumn, bActiveColumn) =>
          (aActiveColumn zip bActiveColumn)
            .toVector
            .traverse(mergeStrategy(state).tupled)
            .map(_.mkString)
      }

  /** Merges a character on the left border */
  private def mergeOnLeftBorder(state: MergeState, section: Columns): MergeAction[Unit] =
    section
      .traverse {
        _.toVector.traverse {
          case ' ' => mergeStrategy(state)(options.font.header.hardblank, ' ')
          case _   => Stop
        }
      }.map(_ => ())

}

private[figlet4s] object Rendering {

  /** Current state of the merge used to provide context to the MergeStrategy */
  final case class MergeState(overlap: Int, currentCharWidth: Int, lastCharWidth: Int)

  /** Function that, given a MergeState, merges two characters and determines the resulting MergeAction */
  type MergeStrategy = MergeState => (Char, Char) => MergeAction[Char]

  /**
   * Renders a String into a FIGure for a given FIGfont and options
   *
   * @param text    The String to render as a FIGure
   * @param options The RenderOptions used to render the text
   * @return A FIGure containing the rendered text following the rendering options
   */
  def render(text: String, options: RenderOptions): FIGure =
    new Rendering(options).render(text)

  //  ----  //

  /** Status of the append loop */
  final protected case class AppendLoopState(lastCharWidth: Int = 0)

  /** Status of the merge loop */
  final protected case class MergeLoopState(
      a: Columns = Vector.empty,
      b: Columns = Vector.empty,
      overlap: Int = 0,
      partialResult: Columns = Vector.empty,
      appendLoopState: AppendLoopState = AppendLoopState(),
  )

  /** Represents the three sections of a set of columns */
  final protected case class Sections(left: Columns, overlap: Columns, right: Columns)

  /** Shortcut for a set of columns */
  protected type Columns = Vector[String]

}
