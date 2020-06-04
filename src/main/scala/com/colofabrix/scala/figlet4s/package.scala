package com.colofabrix.scala

import cats.data._

package object figlet4s {

  /**
   * A result of a processing operation that might include errors
   */
  type FigletResult[A] = Validated[NonEmptyChain[FigletError], A]

}
