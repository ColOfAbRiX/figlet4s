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
 */
final private[rendering] class Rendering(text: String, options: RenderOptions, mergeStrategy: MergeStrategy) {

  private def horizontalRender(): FIGure = {
    val zero    = Vector(options.font.zero.lines.toSubcolumns)
    val figures = text.toList.map(options.font(_).columns).toList
    val result  = appendLoop(figures, zero, AppendLoopState())
    FIGure(options.font, text, result)
  }

  //  ----  //

  private val hardblank: Char = options.font.header.hardblank

  @tailrec
  private def appendLoop(text: List[SubColumns], partial: Vector[SubColumns], state: AppendLoopState): Vector[SubColumns] =
    (text, partial) match {
      case (Nil, _) => partial
      case (figChar :: remainingChars, upperLines :+ lastLine) =>
        val merged    = appendFigures(lastLine, figChar, state)
        val result    = if (merged.length <= options.maxWidth) upperLines :+ merged else partial :+ onBorder(figChar)
        val nextState = state.copy(lastCharWidth = figChar.length)
        appendLoop(remainingChars, result, nextState)
    }

  private def appendFigures(a: SubColumns, b: SubColumns, loopState: AppendLoopState): SubColumns = {
    val initialMergeState = MergeState(a.value.toVector, b.value.toVector, 0, Vector.empty, loopState)
    SubColumns(merge(initialMergeState))
  }

  private def onBorder(figChar: SubColumns): SubColumns = {
    val initialMergeState = MergeState(Vector.empty, figChar.value.toVector, 0, Vector.empty, AppendLoopState())
    SubColumns(merge(initialMergeState))
  }

  //  ----  //

  @tailrec
  private def merge(state: MergeState): Columns =
    if (state.overlap === 0) {
      // printState(state, "NOTHING DO TO, GO FOR OVERLAPPING = 1")
      merge(state.copy(overlap = 1, partialResult = state.a ++ state.b))

    } else if (state.overlap > state.b.length) {
      // printState(state, "RETURNING THE PREVIOUS RESULT")
      // println("=" * 50 + "\n")
      state.partialResult

    } else {
      val aLeftCut  = Math.max(0, state.a.length - state.overlap)
      val aRightCut = Math.min(state.a.length, (state.a.length - state.overlap) + state.b.length)
      val aSections = splitSections(aLeftCut, aRightCut, state.a)

      val bLeftCut  = Math.max(0, state.overlap - state.a.length)
      val bRightCut = Math.min(state.overlap, state.b.length)
      val bSections = splitSections(bLeftCut, bRightCut, state.b)

      val leftSide  = mergeOnLeftBorder(bSections.left)
      val merged    = mergeOverlappingSections(aSections.overlap, bSections.overlap)
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
          merge(state.copy(overlap = state.overlap + 1, partialResult = value))
      }
    }

  /** Merges the two overlapping sections of two characters */
  private def mergeOverlappingSections(aSection: Columns, bSection: Columns): MergeAction[Columns] =
    (aSection zip bSection)
      .traverse {
        case (aActiveColumn, bActiveColumn) =>
          (aActiveColumn zip bActiveColumn)
            .toVector
            .traverse(mergeStrategy.tupled)
            .map(_.mkString)
      }

  /** Merges a character on the left border */
  private def mergeOnLeftBorder(section: Columns): MergeAction[Unit] =
    section
      .traverse {
        _.toVector.traverse {
          case ' ' => mergeStrategy(hardblank, ' ')
          case _   => Stop
        }
      }.map(_ => ())

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

  //  ----  //

  def printState(state: MergeState, result: Object): Unit = {
    def indent(text: Object, amount: Int): String = text.toString.replaceAll("(?m)^", " " * amount)
    println("-" * 50)
    println(s"Overlap: ${state.overlap}")
    println(s"Last char width: ${state.appendLoopState.lastCharWidth}")
    println("  A")
    println(s"    a.length: ${state.a.length}")
    println(indent(SubColumns(state.a), 4))
    println("  B")
    println(s"    b.length: ${state.b.length}")
    println(indent(SubColumns(state.b), 4))
    println("  RESULT")
    println(s"    $result")
  }

}

private[figlet4s] object Rendering {

  final case class AppendLoopState(lastCharWidth: Int = 0)

  /** Status of the merge loop */
  final case class MergeState(
      a: Columns,
      b: Columns,
      overlap: Int,
      partialResult: Columns,
      appendLoopState: AppendLoopState,
  )

  /** Represents the three sections of a set of columns */
  final protected case class Sections(left: Columns, overlap: Columns, right: Columns)

  /** Function that merges two characters */
  type MergeStrategy = (Char, Char) => MergeAction[Char]

  /** Function that smushes two characters */
  type SmushingStrategy = (Char, Char) => Option[Char]

  /** Shortcut for a set of columns */
  protected type Columns = Vector[String]

  /**
   * Renders a text horizontally by merging one character after the other
   *
   * @param text          The text to render
   * @param options       The options of the rendering
   * @param mergeStrategy The merge strategy used to merge together two characters
   * @return A new FIGure that corresponds to the rendered text
   */
  def horizontalRender(text: String, options: RenderOptions, mergeStrategy: MergeStrategy): FIGure =
    new Rendering(text, options, mergeStrategy).horizontalRender()

}
