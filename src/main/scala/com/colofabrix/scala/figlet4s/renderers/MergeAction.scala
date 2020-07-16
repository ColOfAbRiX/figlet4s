package com.colofabrix.scala.figlet4s.renderers

import cats._
import cats.data._
import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont.SubColumns

/**
 * Result of the merge of data like SubColumns
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
}
