package com.colofabrix.scala.figlet4s

import sys.process.*

/**
 * Compatibility module for Scala 3
 */
private[figlet4s] object compat {
  /** Dummy method to prevent "unused import" errors */
  def ->(): Unit = ()

  extension (self: List[String])
    def runStream: LazyList[String] = self.lazyLines
}
