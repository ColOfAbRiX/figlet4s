package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.unsafe._
import org.scalameter.api._
import com.colofabrix.scala.figlet4s.figfont._

final class Figlet4sUnsafeBenchmark extends LocalHTMLReporter {

  private val sizes: Gen[Int] = Gen.range("size")(1, 2000, 100)

  private val rnd = new scala.util.Random(System.currentTimeMillis())

  private def randomString(font: FIGfont, length: Int): String = {
    val charSet = font.characters.keySet.toVector
    List.fill(length)(charSet(rnd.nextInt(charSet.size))).mkString
  }

  performance of "Figlet4s" in {

    // I'm interested in comparing Vector vs List for the rendering of text
    measure method "renderString" in {
      val options = Figlet4s.builder().options
      using(sizes) in { length =>
        Figlet4s.renderString(randomString(options.font, length), options)
      }
    }

  }

}
