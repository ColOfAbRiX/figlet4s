package com.colofabrix.scala.figlet4s

import sys.process._

/**
 * Dummy compatibility module
 */
private[figlet4s] object compat {
  /** Dummy method to prevent "unused import" errors */
  def ->(): Unit = ()

  implicit class CompatListString(private val self: List[String]) extends AnyVal {
    def runStream: LazyList[String] = self.lazyLines
  }
}
