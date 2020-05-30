package com.colofabrix.scala

import cats.data._

package object figlet4s {

  type FigletResult[A] = Validated[NonEmptyChain[FigletError], A]

}
