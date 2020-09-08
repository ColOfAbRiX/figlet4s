package com.colofabrix.scala.figlet4s.catsio

import cats.effect.IO
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._

private[catsio] trait FIGureOps {

  implicit class FIGureOps(val self: FIGure) extends FIGureAPI[IO] with FIGureEffectfulAPI[IO] {
    /** Apply a function to each line of the FIGure */
    def foreachLine[A](f: String => A): IO[Unit] = IO {
      self.cleanLines.foreach(_.foreach(f))
    }

    /** Print the FIGure */
    def print(): IO[Unit] =
      self.foreachLine(println)

    /** The figure as a collection of String, one String per displayed line */
    def asSeq(): Vector[String] =
      asVectorF().unsafeRunSync()

    /** The figure as single String and newline characters */
    def asString(): String =
      asStringF().unsafeRunSync()

    /** The figure as a Vector of String */
    def asVectorF(): IO[Vector[String]] = IO.pure {
      self.cleanLines.map(_.value.mkString("\n"))
    }

    /** The figure as single String */
    def asStringF(): IO[String] =
      asVectorF().map(_.mkString("\n"))
  }

}
