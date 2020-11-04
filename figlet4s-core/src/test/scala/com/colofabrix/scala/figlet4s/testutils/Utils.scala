package com.colofabrix.scala.figlet4s.testutils

import cats.effect._

object Utils {
  def touch[A](a: A): Unit = (IO(a) *> IO.unit).unsafeRunSync()
}
