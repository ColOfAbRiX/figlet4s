package com.colofabrix.scala.figlet4s

import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest
import scala.collection.immutable.BitSet
import scala.util.matching.Regex
import sys.process.*

/**
 * Compatibility module for Scala 3
 *
 * Contains extension methods that are implemented as implicit classes in Scala 2.13
 */
@SuppressWarnings(Array("org.wartremover.warts.Throw"))
private[figlet4s] object compat {

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
