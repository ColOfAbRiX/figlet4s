package com.colofabrix.scala.figlet4s

import scala.collection.immutable.BitSet

object Utils {
  /**
   * Adds conversion to BitSet to Int
   */
  implicit class BinaryInt(val self: Int) extends AnyVal {
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
}
