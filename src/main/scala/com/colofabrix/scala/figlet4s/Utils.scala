package com.colofabrix.scala.figlet4s

import _root_.cats._
import _root_.cats.arrow.FunctionK
import _root_.cats.implicits._
import java.math.BigInteger
import java.security.MessageDigest
import scala.collection.immutable.BitSet
import scala.language.higherKinds

private[figlet4s] object Utils {
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

  implicit class MD5String(val self: String) extends AnyVal {
    /**
     * MD5 hash of the string
     */
    def md5(): String = {
      val md     = MessageDigest.getInstance("MD5")
      val digest = md.digest(self.getBytes)
      val bigInt = new BigInteger(1, digest)
      bigInt.toString(16)
    }
  }

  // Traversable from Isomorphism
  // See https://stackoverflow.com/a/48833659/1215156

  private def traverseFromIso[F[_], Z[_]](forward: F ~> Z, inverse: Z ~> F)(
      implicit
      zt: Traverse[Z],
  ): Traverse[F] =
    new Traverse[F] {
      def foldLeft[A, B](fa: F[A], b: B)(f: (B, A) => B): B =
        zt.foldLeft(forward(fa), b)(f)

      def foldRight[A, B](fa: F[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
        zt.foldRight(forward(fa), lb)(f)

      def traverse[G[_], A, B](fa: F[A])(f: (A) => G[B])(
          implicit
          appG: Applicative[G],
      ): G[F[B]] =
        (zt.traverse(forward(fa))(f)(appG)).map(zb => inverse(zb))
    }

  implicit val seqTraverse: Traverse[Seq] = traverseFromIso(
    new FunctionK[Seq, Vector] { def apply[X](sx: Seq[X]): Vector[X] = sx.toVector },
    new FunctionK[Vector, Seq] { def apply[X](vx: Vector[X]): Seq[X] = vx          },
  )

  implicit val indexedSeqTraverse: Traverse[IndexedSeq] = traverseFromIso(
    new FunctionK[IndexedSeq, Vector] { def apply[X](isx: IndexedSeq[X]): Vector[X] = isx.toVector   },
    new FunctionK[Vector, IndexedSeq] { def apply[X](vx: Vector[X]): IndexedSeq[X] = vx.toIndexedSeq },
  )
}
