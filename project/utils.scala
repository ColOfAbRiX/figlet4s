import sbt._

object utils {

  def versioned[A](version: String)(for213: => A, for3: => A): A =
    CrossVersion.partialVersion(version) match {
      case Some((2, 13)) => for213
      case Some((3, _))  => for3
      case _             => for3 // Default to Scala 3 for unknown versions
    }

}
