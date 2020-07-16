package com.colofabrix.scala.figlet4s.renderers

import cats._
import cats.data._
import cats.implicits._
import scala.annotation.tailrec
import com.colofabrix.scala.figlet4s.figfont.SubColumns

/*
Explanation of the general algorithm

Merged FIGures (using Horizontal Fitting as example renderer):

   FIGure A   FIGure B     Resulting FIGure
  /        \ /       \           |
  +-----+---+---+-----+    +-----+---+-----+
  |  ___|__ |   |     |    |  ___|__ |     |
  | |  _|__||   |__ _ |    | |  _|__||__ _ |
  | | |_|   |  /| _` || -> | | |_|  /| _` ||
  | |  _||  | | |(_| || -> | |  _||| |(_| ||
  | |_| |   |  \|__,_||    | |_| |  \|__,_||
  |     |   |   |     |    |     |   |     |
  +-----+---+---+-----+    +-----+---+-----+
         \     /                   |
       Overlap area             Merged

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
 FIGure B                                        |             |
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

NOTES:
- Each recursive call of the algorithm works with one active column from each FIGure at the time.
- A custom merge strategy function is used to determine how the two active columns are merged.
- The custom merge function works by accepting corresponding character from the two active column and by returning the
  character resulting from their merger. Together with this the function can instruct the algorithm on how to proceed
  next.
- Once the two columns have been merged the algorithm decides what to do next:
   - It stores the result of the merge and does a recursive call to process the next column
   - It stores the result of the merge and stops more processing
   - It uses the result of the merge of the previous recursive call and stops more processing
 */

sealed trait MergeAction[+A]

object MergeAction {

  /** Represents a "keep the value and continue" condition */
  final case class Continue[A](value: A)    extends MergeAction[A]
  /** Represents a "stop processing, keep the value" condition */
  final case class CurrentLast[A](value: A) extends MergeAction[A]
  /** Represents a "stop processing, use last value" condition */
  final case object Stop                    extends MergeAction[Nothing]

  implicit val applicativeMergeAction: Applicative[MergeAction] = new Applicative[MergeAction] {
    def pure[A](x: A): MergeAction[A] =
      Continue(x)

    def ap[A, B](ff: MergeAction[A => B])(fa: MergeAction[A]): MergeAction[B] =
      (ff, fa) match {
        case (_, Stop)                            => Stop
        case (Stop, _)                            => Stop
        case (Continue(f), Continue(value))       => Continue(f(value))
        case (Continue(f), CurrentLast(value))    => CurrentLast(f(value))
        case (CurrentLast(f), Continue(value))    => CurrentLast(f(value))
        case (CurrentLast(f), CurrentLast(value)) => CurrentLast(f(value))
      }
  }

  def process(a: SubColumns, b: SubColumns)(f: (Char, Char) => MergeAction[Char]): SubColumns =
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
