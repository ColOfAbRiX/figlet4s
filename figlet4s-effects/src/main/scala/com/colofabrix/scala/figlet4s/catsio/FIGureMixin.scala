package com.colofabrix.scala.figlet4s.catsio

import cats.effect.IO
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._

private[catsio] trait FIGureMixin {

  implicit class FIGureOps(val self: FIGure) extends FIGureAPI[IO] with FIGureEffectfulAPI[IO] {
    /** @inheritdoc */
    def foreachLine[A](f: String => A): IO[Unit] = IO {
      self.cleanLines.foreach(_.foreach(f))
    }

    /** @inheritdoc */
    def print(): IO[Unit] =
      self.foreachLine(println)

    /** @inheritdoc */
    def asSeq(): Seq[String] =
      asSeqF().unsafeRunSync()

    /** @inheritdoc */
    def asString(): String =
      asStringF().unsafeRunSync()

    /** @inheritdoc */
    def asSeqF(): IO[Seq[String]] = IO.pure {
      self.cleanLines.map(_.value.mkString("\n"))
    }

    /** @inheritdoc */
    def asStringF(): IO[String] =
      asSeqF().map(_.mkString("\n"))
  }

}
