package com.colofabrix.scala.figlet4s.catsio

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.implicits.toTraverseOps
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.figfont._

private[catsio] trait FIGureMixin {

  implicit class FIGureOps(val self: FIGure)(implicit ioRuntime: IORuntime)
      extends FIGureAPI[IO]
      with FIGureEffectfulAPI[IO] {
    /**
     * Apply a function to each line of the FIGure
     *
     * @param f The function to applied to each displayable line
     */
    def foreachLine[A](f: String => A): IO[Unit] =
      self.cleanLines.toList.flatTraverse(_.value.toList.traverse(line => IO(f(line)))) >>
      IO.unit

    /**
     * Print the FIGure to standard output
     */
    def print(): IO[Unit] =
      self.foreachLine(println)

    /**
     * The figure as a collection of String, one String per displayable line
     *
     * @return A collection of strings, each containing a displayable line
     */
    def asSeq(): Seq[String] =
      asSeqF().unsafeRunSync()

    /**
     * The figure as single String and newline characters
     *
     * @return A single string containing the FIGure including newlines where needed
     */
    def asString(): String =
      asStringF().unsafeRunSync()

    /**
     * The figure as a collection of String, one String per displayable line
     *
     * @return A collection of strings, each containing a displayable line
     */
    def asSeqF(): IO[Seq[String]] = IO.pure {
      self.cleanLines.map(_.value.mkString(System.lineSeparator()))
    }

    /**
     * The figure as single String and newline characters
     *
     * @return A single string containing the FIGure including newlines where needed
     */
    def asStringF(): IO[String] =
      asSeqF().map(_.mkString(System.lineSeparator()))
  }

}
