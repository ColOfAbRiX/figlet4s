package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.errors.*
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest
import scala.collection.immutable.BitSet
import scala.util.*
import scala.util.matching.Regex
import sys.process.*

/**
 * Compatibility module for Scala 3
 *
 * Contains extension methods that are implemented as implicit classes in Scala 2.13
 */
@SuppressWarnings(Array("org.wartremover.warts.Throw"))
private[figlet4s] object compat {

  /** Dummy method to prevent "unused import" errors */
  def ->(): Unit = ()

  extension (self: List[String]) {

    def runStream: LazyList[String] = self.lazyLines

  }

  extension (self: Int) {

    /** Converts the Int to a BitSet */
    def toBitSet: BitSet = BitSet.fromBitMask(Array(self.toLong))

  }

  extension (self: String) {

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

  extension (sc: StringContext) {

    def r: Regex = new Regex(sc.parts.mkString, sc.parts.drop(1).map(_ => "x")*)

  }

}

import cats.implicits.*
import cats.data.*

/**
 * Validated extension methods for Scala 3
 */
@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf", "org.wartremover.warts.Throw"))
private[figlet4s] object validatedCompat {

  extension (self: FigletResult[?]) {

    @throws(classOf[FigletException])
    def unsafeGet[A]: A = self.asInstanceOf[FigletResult[A]].fold(e => throw e.head, identity)

  }

  extension [A](self: Try[A]) {

    def toFigletResult: FigletResult[A] =
      self match {
        case Failure(exception) => FigletError(exception).invalidNec
        case Success(value)     => value.validNec
      }

  }

}
