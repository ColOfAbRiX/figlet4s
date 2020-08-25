package com.colofabrix.scala.figlet4s.unsafeops

import com.colofabrix.scala.figlet4s.errors._

private[figlet4s] object FigletResultOps {

  /** Unsafely returns the value inside the FigletResult or throws an exception with the first error */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit class FigletResultOps[E, A](val self: FigletResult[A]) extends AnyVal {
    def unsafeGet: A = self.fold(e => throw e.head, identity)
  }

}
