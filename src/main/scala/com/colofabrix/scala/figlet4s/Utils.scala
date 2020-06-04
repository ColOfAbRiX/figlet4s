package com.colofabrix.scala.figlet4s

import java.math.BigInteger
import java.security.MessageDigest
import scala.collection.immutable.BitSet

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
}
