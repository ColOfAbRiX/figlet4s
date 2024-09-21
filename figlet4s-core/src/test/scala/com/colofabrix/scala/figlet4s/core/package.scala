package com.colofabrix.scala.figlet4s

import cats._

package object core {

  implicit private[core] val monadThrowId: MonadThrow[Id] = new MonadThrow[Id] {
    private val M: Monad[Id] = Monad[Id](catsInstancesForId)

    override def pure[A](x: A): Id[A] =
      M.pure(x)

    override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
      M.flatMap(fa)(f)

    override def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] =
      M.tailRecM(a)(f)

    override def raiseError[A](e: Throwable): Id[A] =
      throw e

    override def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
      throw new NotImplementedError("MonadThrow[Id] does not support ApplicativeError.handleErrorWith")
  }

}
