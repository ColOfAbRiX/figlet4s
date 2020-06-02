package com.colofabrix.scala.figlet4s

import _root_.cats._
import _root_.cats.implicits._
import com.colofabrix.scala.figlet4s.figfont.FIGfont
import scala.annotation.tailrec

/**
 * Cats wrapper for Figlet4s
 */
final case class FIGcats[A](value: A) {
  def toFIGstring(font: FIGfont)(implicit S: Show[A]): FIGstring =
    FIGstring(S.show(value), font)
}

object FIGcats {
  def fromFIGstring(figstring: FIGstring): FIGcats[String] = FIGcats(figstring.value)
}

package object cats {

  // /**
  //  * FIGCats Operations
  //  */
  // implicit class FIGCatsOps[A](figcats: FIGcats[A])(implicit M: Monad[FIGcats], F: Foldable[FIGcats]) {
  //   def pure(x: A): FIGcats[A] =
  //     M.pure(x)

  //   def map[B](f: A => B): FIGcats[B] =
  //     M.map(figcats)(f)

  //   def flatMap[B](f: A => FIGcats[B]): FIGcats[B] =
  //     M.flatMap(figcats)(f)

  //   def ap[B](ff: FIGcats[A => B]): FIGcats[B] =
  //     M.ap(ff)(figcats)

  //   def foldLeft[B](b: B)(f: (B, A) => B): B =
  //     F.foldLeft(figcats, b)(f)
  // }

  /**
   * FIGCats Semigroup Operations for Show[A]
   */
  implicit class FIGCatsShowSemigroupOps[A: Show](figcats: FIGcats[A])(implicit S: Semigroup[FIGcats[A]]) {
    def combine(x: FIGcats[A], y: FIGcats[A]): FIGcats[A] =
      Semigroup[FIGcats[A]].combine(x, y)
  }

  /**
   * FIGCats Monoidal Operations for Show[A]
   */
  implicit class FIGCatsShowMonoidOps[A: Show](figcats: FIGcats[A])(implicit M: Monoid[FIGcats[A]]) {
    def empty: FIGcats[A] =
      M.empty

    def combine(x: FIGcats[A], y: FIGcats[A]): FIGcats[A] =
      M.combine(x, y)
  }

  /**
   * FIGcats Functor
   */
  implicit def functorFIGcats = new Functor[FIGcats] {
    def map[A, B](fa: FIGcats[A])(f: A => B): FIGcats[B] = fa.copy(value = f(fa.value))
  }

  /**
   * FIGcats Applicative
   */
  implicit def applicativeFIGcats = new Applicative[FIGcats] {
    def pure[A](x: A): FIGcats[A] =
      FIGcats(x)

    def ap[A, B](ff: FIGcats[A => B])(fa: FIGcats[A]): FIGcats[B] =
      Functor[FIGcats].map(fa)(a => Functor[FIGcats].map(ff)(f => f(a)).value)
  }

  /**
   * FIGcats Monad
   */
  implicit def monadFIGcats = new Monad[FIGcats] {
    def pure[A](x: A): FIGcats[A] =
      Applicative[FIGcats].pure(x)

    def flatMap[A, B](fa: FIGcats[A])(f: A => FIGcats[B]): FIGcats[B] =
      f(fa.value)

    @tailrec
    def tailRecM[A, B](a: A)(f: A => FIGcats[Either[A, B]]): FIGcats[B] =
      f(a) match {
        case FIGcats(Left(a))  => tailRecM(a)(f)
        case FIGcats(Right(b)) => FIGcats(b)
      }
  }

  /**
   * FIGcats Foldable
   */
  implicit def foldableFIGcats = new Foldable[FIGcats] {
    def foldLeft[A, B](fa: FIGcats[A], b: B)(f: (B, A) => B): B =
      f(b, fa.value)

    def foldRight[A, B](fa: FIGcats[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
      f(fa.value, lb)
  }

  /**
   * FIGcats Semigroup
   */
  implicit def semigroupFIGcats[A: Semigroup] = new Semigroup[FIGcats[A]] {
    def combine(x: FIGcats[A], y: FIGcats[A]): FIGcats[A] =
      FIGcats(Semigroup[A].combine(x.value, y.value))
  }

  /**
   * FIGcats Monoid
   */
  implicit def monoidFIGcats[A: Monoid] = new Monoid[FIGcats[A]] {
    def empty: FIGcats[A] =
      FIGcats(Monoid[A].empty)

    def combine(x: FIGcats[A], y: FIGcats[A]): FIGcats[A] =
      FIGcats(Monoid[A].combine(x.value, y.value))
  }
}
