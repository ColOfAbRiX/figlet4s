package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.errors._
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest
import scala.collection.immutable.BitSet
import scala.util._
import scala.util.matching.Regex
import sys.process._

/**
 * Compatibility module for Scala 2.13
 *
 * Contains implicit classes that are implemented as extension methods in Scala 3
 */
@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
private[figlet4s] object compat {
  /** Dummy method to prevent "unused import" errors */
  def ->(): Unit = ()

  implicit class CompatListString(private val self: List[String]) extends AnyVal {
    def runStream: LazyList[String] = self.lazyLines
  }

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

import cats.implicits._

/**
 * Validated extension methods for Scala 2.13
 */
@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
private[figlet4s] object validatedCompat {
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit class FigletResultOps[A](private val self: FigletResult[A]) extends AnyVal {
    @throws(classOf[FigletException])
    def unsafeGet: A = self.fold(e => throw e.head, identity)
  }

  implicit class FigletTry[A](private val self: Try[A]) extends AnyVal {
    def toFigletResult: FigletResult[A] =
      self match {
        case Failure(exception) => FigletError(exception).invalidNec
        case Success(value)     => value.validNec
      }
  }
}
