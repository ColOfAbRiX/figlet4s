package com.colofabrix.scala.figlet4s.unsafe

import cats._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._

private[unsafe] trait FIGureMixin {

  implicit class FIGureOps(val self: FIGure) extends FIGureAPI[Id] {
    /** @inheritdoc */
    def foreachLine[A](f: String => A): Unit =
      self.cleanLines.foreach(_.foreach(f))

    /** @inheritdoc */
    def print(): Unit =
      self.foreachLine(println)

    /** @inheritdoc */
    def asSeq(): Seq[String] =
      self.cleanLines.flatMap(_.value)

    /** @inheritdoc */
    def asString(): String =
      asSeq().mkString("\n")
  }

}
