package sbtproject

import sbt._

object utils {

  def versioned[A](version: String)(for212: => A, for213: => A): A =
    CrossVersion.partialVersion(version) match {
      case Some((2, n)) if n == 12 => for212
      case Some((2, n)) if n == 13 => for213
    }

}
