package com.colofabrix.scala.figlet4s

import scala.util.Try
import sys.process._

/**
 * Compatibility extension methods for Scala 2.12 -> Scala 2.13
 */
private[figlet4s] object compat {

  /** Dummy method to prevent "unused import" errors */
  def ->(): Unit = ()

  implicit class CompatString(private val self: String) extends AnyVal {
    def toIntOption: Option[Int] = Try(self.toInt).toOption
  }

  implicit class CompatListString(private val self: List[String]) extends AnyVal {
    def runStream: Stream[String] = self.lineStream
  }

  implicit class CompatOptionType(private val self: Option.type) extends AnyVal {
    def when[A](cond: Boolean)(a: => A): Option[A] = if (cond) Some(a) else None
  }

  implicit class CompatSeq[A](private val self: Seq[A]) extends AnyVal {
    @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
    def maxOption[B >: A](implicit ord: Ordering[B]): Option[A] = if (self.isEmpty) None else Some(self.max(ord))
  }

}
