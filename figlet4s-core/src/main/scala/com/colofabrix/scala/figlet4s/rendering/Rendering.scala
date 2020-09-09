package com.colofabrix.scala.figlet4s.rendering

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont.SubColumns
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

  /**
   * Merges two columns applying a custom merge function to each pair of character of the two columns
   */
  def mergeColumnWith(f: (Char, Char) => MergeAction[Char]): MergeStrategy = { (a, b) =>
    val height = Math.max(
      a.value.headOption.map(_.length()).getOrElse(0),
      b.value.headOption.map(_.length()).getOrElse(0),
    )
    val aColumn = Vector("$" * height) ++ a.value.toVector
    val bColumn = b.value.toVector ++ Vector("$" * height)
    val result  = merge(aColumn, bColumn, 0, Vector.empty)(f)

    println(s"RESULT: $result")

    SubColumns(result)
  }

  @tailrec
  private def merge(a: Vector[String], b: Vector[String], overlap: Int, previous: Vector[String])(
      f: (Char, Char) => MergeAction[Char],
  ): Vector[String] = {
    println(s"Overlap: $overlap")

    if (overlap === 0) {
      println("Zero overlap\n")
      merge(a, b, 1, a ++ b)(f)

    } else if (overlap > a.length && overlap > b.length) {
      println("Overlap bigger than both parts\n")
      previous

      // } else if (overlap > a.length) {
      //   val hardblankColumn = Vector("$" * b.head.length)
      //   merge(hardblankColumn ++ a, b, overlap, previous)(f)

      // } else if (overlap > b.length) {
      //   ???

    } else {
      // Split the columns into left, right, A-overlapping and B-overlapping
      val (left, aOverlap)  = a.splitAt(Math.max(0, a.length - overlap))
      val (bOverlap, right) = b.splitAt(overlap)

      println(s"First split at: ${Math.max(0, a.length - overlap)}")
      println(s"Second split at: ${overlap}")
      println(s"left: $left")
      println(s"aOverlap: $aOverlap")
      println(s"bOverlap: $bOverlap")
      println(s"right: $right")
      println("")

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
        case Stop                 => previous
        case CurrentLast(current) => current
        case Continue(value)      => merge(a, b, overlap + 1, value)(f)
      }
    }
  }

}
