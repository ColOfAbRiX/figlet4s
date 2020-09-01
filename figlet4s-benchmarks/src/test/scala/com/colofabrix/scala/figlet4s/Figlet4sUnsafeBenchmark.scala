package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.unsafe._
import org.scalameter.api._

final class Figlet4sUnsafeBenchmark extends LocalHTMLReporter {

  private lazy val sizes: Gen[Int] = Gen.range("size")(1, 1000, 100)

  private val rnd     = new scala.util.Random(System.currentTimeMillis())
  private val charSet = ((32 to 126) ++ Seq(196, 214, 220, 228, 246, 252, 223)).map(_.toChar)

  private def randomString(length: Int): String =
    List.fill(length)(charSet(rnd.between(0, charSet.length))).mkString

  performance of "Figlet4s" in {

    // I'm interested in comparing Vector vs List for the rendering of text
    measure method "renderString" in {
      val options = Figlet4s.builder().options
      using(sizes) in { length =>
        Figlet4s.renderString(randomString(length), options)
      }
    }

  }

}
