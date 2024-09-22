package com.colofabrix.scala.figlet4s.figfont

import cats.data.ValidatedNel
import com.colofabrix.scala.figlet4s.errors._

trait SpecOps {
  protected def bindIdentity[A, B]: (A, B) => Vector[A] =
    (x: A, _: B) => Vector(x)

  protected def adaptError[A](value: FigletResult[A]): ValidatedNel[String, A] =
    value.leftMap { errors =>
      errors.toNonEmptyList.map(error => s"${error.getClass.getSimpleName} - ${error.getMessage}")
    }

  protected val font: TestFont = TestFont()
  protected val header: TestHeader = TestHeader()

}
