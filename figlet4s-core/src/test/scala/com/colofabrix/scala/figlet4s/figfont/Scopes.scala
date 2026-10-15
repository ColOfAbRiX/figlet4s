package com.colofabrix.scala.figlet4s.figfont

import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.errors._

trait ScopesUtils {

  protected def bindIdentity[A, B]: (A, B) => Vector[A] =
    (x: A, _: B) => Vector(x)

  protected def adaptError[A](value: FigletResult[A]): FigletResult[A] =
    value.mapErrors(error => FigletError(s"${error.getClass.getSimpleName} - ${error.getMessage}"))

}

trait HeaderScope extends ScopesUtils {

  final val header: TestHeader = TestHeader()

}

trait CharacterScope extends HeaderScope {

  final val character = TestCharacter

}

trait FontScope extends CharacterScope {

  final val font: TestFont = TestFont()

}
