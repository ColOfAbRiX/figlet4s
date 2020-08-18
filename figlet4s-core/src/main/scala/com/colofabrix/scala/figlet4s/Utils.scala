package com.colofabrix.scala.figlet4s

import cats._
import cats.arrow.FunctionK
import cats.effect._
import cats.implicits._
import java.math.BigInteger
import java.security.MessageDigest
import scala.annotation.tailrec
import scala.collection.immutable.BitSet

private[figlet4s] object utils {

  /**
   * Common trait for ADTs
   */
  trait ADT extends Product with Serializable

  //  Traversable from Isomorphism (https://stackoverflow.com/a/48833659/1215156)  //

  /**
   * Traversable instance for Seq
   */
  implicit val seqTraverse: Traverse[Seq] = traverseFromIso(
    new FunctionK[Seq, Vector] { def apply[X](sx: Seq[X]): Vector[X] = sx.toVector },
    new FunctionK[Vector, Seq] { def apply[X](vx: Vector[X]): Seq[X] = vx          },
  )

  /**
   * Traversable instance for IndexedSeq
   */
  implicit val indexedSeqTraverse: Traverse[IndexedSeq] = traverseFromIso(
    new FunctionK[IndexedSeq, Vector] { def apply[X](isx: IndexedSeq[X]): Vector[X] = isx.toVector },
    new FunctionK[Vector, IndexedSeq] { def apply[X](vx: Vector[X]): IndexedSeq[X] = vx            },
  )

  private def traverseFromIso[F[_], Z[_]](forward: F ~> Z, inverse: Z ~> F)(
      implicit
      zt: Traverse[Z],
  ): Traverse[F] =
    new Traverse[F] {
      def foldLeft[A, B](fa: F[A], b: B)(f: (B, A) => B): B =
        zt.foldLeft(forward(fa), b)(f)

      def foldRight[A, B](fa: F[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
        zt.foldRight(forward(fa), lb)(f)

      def traverse[G[_], A, B](fa: F[A])(f: A => G[B])(
          implicit
          appG: Applicative[G],
      ): G[F[B]] =
        zt.traverse(forward(fa))(f)(appG).map(zb => inverse(zb))
    }

  //  Sync[Id]  //

  /**
   * Sync instance for Id for impure calculations
   */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit val syncId: Sync[Id] = new Sync[Id] {
    import scala.util._

    def pure[A](x: A): Id[A] =
      x

    def suspend[A](thunk: => Id[A]): Id[A] =
      thunk

    def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
      f(fa)

    @tailrec
    def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] =
      f(a) match {
        case Left(value)  => tailRecM(value)(f)
        case Right(value) => value
      }

    def bracketCase[A, B](resource: Id[A])(use: A => Id[B])(release: (A, ExitCase[Throwable]) => Id[Unit]): Id[B] =
      Try(use(resource)) match {
        case Failure(error) =>
          release(resource, ExitCase.Error(error))
          throw error
        case Success(result) =>
          release(resource, ExitCase.Completed)
          result
      }

    def raiseError[A](e: Throwable): Id[A] =
      throw e

    def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
      fa
  }

  //  Misc  //

  /**
   * Enrichment methods for Int for binary conversion
   */
  implicit class BinaryInt(val self: Int) extends AnyVal {
    /**
     * Converts the Int to a BitSet
     */
    def toBitSet(size: Int): BitSet = {
      val binaryString = self.toBinaryString

      val bitSet = binaryString
        .substring(0, Math.min(binaryString.length, Math.max(size, 32)) - 1)
        .reverse
        .zipWithIndex
        .flatMap {
          case ('0', _) => IndexedSeq()
          case ('1', n) => IndexedSeq(n)
          case (_, _)   => IndexedSeq() // Never gets here thanks to .toBinaryString
        }

      BitSet.fromSpecific(bitSet)
    }

    def toBitSet: BitSet = toBitSet(32)
  }

  /**
   * Enrichment methods for String to calculate the MD5 hash
   */
  implicit class MD5String(val self: String) extends AnyVal {
    /**
     * MD5 hash of the string
     */
    def md5: String = {
      val md     = MessageDigest.getInstance("MD5")
      val digest = md.digest(self.getBytes)
      val bigInt = new BigInteger(1, digest)
      bigInt.toString(16)
    }
  }

}
