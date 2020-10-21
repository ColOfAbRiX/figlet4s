package com.colofabrix.scala.figlet4s

import _root_.cats.effect._
import com.colofabrix.scala.figlet4s.errors._

package object catsio extends FIGureMixin with OptionsBuilderMixin {

  /** Transforms the FigletResult into a Cat's IO capturing the first error in IO */
  implicit private[catsio] class FigletResultOps[E, A](private val self: FigletResult[A]) extends AnyVal {
    def asIO: IO[A] = self.fold(e => IO.raiseError(e.head), IO(_))
  }

}
