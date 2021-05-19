package com.colofabrix.scala.figlet4s

import cats._
import cats.arrow.FunctionK
import cats.implicits._
import java.math.BigInteger
import java.security.MessageDigest
import scala.collection.immutable.BitSet
import scala.util.matching.Regex
import java.nio.charset.Charset

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

  private def traverseFromIso[F[_], Z[_]](forward: F ~> Z, inverse: Z ~> F)(implicit zt: Traverse[Z]): Traverse[F] =
    new Traverse[F] {
      def foldLeft[A, B](fa: F[A], b: B)(f: (B, A) => B): B =
        zt.foldLeft(forward(fa), b)(f)

      def foldRight[A, B](fa: F[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
        zt.foldRight(forward(fa), lb)(f)

      def traverse[G[_], A, B](fa: F[A])(f: A => G[B])(implicit appG: Applicative[G]): G[F[B]] =
        zt.traverse(forward(fa))(f)(appG).map(zb => inverse(zb))
    }

  //  Enrichment methods  //

  /**
   * Enrichment methods for Int for binary conversion
   */
  implicit final class BinaryInt(private val self: Int) extends AnyVal {
    /** Converts the Int to a BitSet */
    def toBitSet: BitSet = BitSet.fromBitMask(Array(self.toLong))
  }

  /**
   * Enrichment methods for String to calculate the MD5 hash
   */
  implicit final class MD5String(private val self: String) extends AnyVal {
    /**
     * MD5 hash of the string
     */
    def md5: String = {
      val md     = MessageDigest.getInstance("MD5")
      val digest = md.digest(self.getBytes(Charset.forName("ISO-8859-1")))
      val bigInt = new BigInteger(1, digest)
      bigInt.toString(16)
    }
  }

  /**
   * Enrichment methods for StringContext
   */
  implicit class RegexContext(sc: StringContext) {
    def r = new Regex(sc.parts.mkString, sc.parts.drop(1).map(_ => "x"): _*)
  }
}
