package com.colofabrix.scala.figlet4s.rendering

/**
 * Result of the merge of data like SubColumns
 */
sealed private[figlet4s] trait MergeAction[@specialized(Char) +A]

private[figlet4s] object MergeAction {

  /** Represents a "keep the value and continue" condition */
  final case class Continue[@specialized(Char) A](value: A) extends MergeAction[A]

  /** Represents a "stop processing, keep use the value of the current iteration" condition */
  final case class CurrentLast[@specialized(Char) A](value: A) extends MergeAction[A]

  /** Represents a "stop processing, use value of last iteration" condition */
  case object Stop extends MergeAction[Nothing]

  /**
   * Combines two actions: Stop wins over everything and CurrentLast wins over Continue
   */
  def map2[A, B, C](fa: MergeAction[A], fb: MergeAction[B])(f: (A, B) => C): MergeAction[C] =
    (fa, fb) match {
      case (Stop, _)                         => Stop
      case (_, Stop)                         => Stop
      case (CurrentLast(a), CurrentLast(b))  => CurrentLast(f(a, b))
      case (CurrentLast(a), Continue(b))     => CurrentLast(f(a, b))
      case (Continue(a), CurrentLast(b))     => CurrentLast(f(a, b))
      case (Continue(a), Continue(b))        => Continue(f(a, b))
    }

  /**
   * Combines a sequence of actions into one action holding all the values, with the same precedence rules of map2
   */
  def traverseSeq[A, B](values: Seq[A])(f: A => MergeAction[B]): MergeAction[Vector[B]] =
    values.foldLeft[MergeAction[Vector[B]]](Continue(Vector.empty)) { (acc, value) =>
      (acc, f(value)) match {
        case (Stop, _)                          => Stop
        case (_, Stop)                          => Stop
        case (CurrentLast(vs), Continue(v))     => CurrentLast(vs :+ v)
        case (Continue(vs), CurrentLast(v))     => CurrentLast(vs :+ v)
        case (CurrentLast(vs), CurrentLast(v))  => CurrentLast(vs :+ v)
        case (Continue(vs), Continue(v))        => Continue(vs :+ v)
      }
    }

  /**
   * Syntax on single actions
   */
  implicit class MergeActionOps[A](private val action: MergeAction[A]) extends AnyVal {

    def map[B](f: A => B): MergeAction[B] =
      action match {
        case Continue(value)    => Continue(f(value))
        case CurrentLast(value) => CurrentLast(f(value))
        case Stop               => Stop
      }

    def mapN[B, C](other: MergeAction[B])(f: (A, B) => C): MergeAction[C] =
      MergeAction.map2(action, other)(f)

  }

  /**
   * Syntax on 3-tuples of actions
   */
  implicit class MergeActionTuple3Ops[A, B, C](
      private val tuple: (MergeAction[A], MergeAction[B], MergeAction[C]),
  ) extends AnyVal {

    def mapN[D](f: (A, B, C) => D): MergeAction[D] = {
      val ab = MergeAction.map2(tuple._1, tuple._2)((_, _))
      MergeAction.map2(ab, tuple._3) { case ((a, b), c) =>
        f(a, b, c)
      }
    }

  }

}
