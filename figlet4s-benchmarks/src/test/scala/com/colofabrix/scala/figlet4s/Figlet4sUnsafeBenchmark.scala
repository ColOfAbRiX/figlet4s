package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.unsafe._
import org.scalameter.api._
import scala.util._

final class Figlet4sUnsafeBenchmark extends LocalHTMLReporter {

  private lazy val sizes: Gen[Int] = Gen.range("size")(1, 1000, 100)

  private val fonts   = Figlet4s.internalFonts
  private val rnd     = new scala.util.Random(System.currentTimeMillis())
  private val charSet = ((32 to 126) ++ Seq(196, 214, 220, 228, 246, 252, 223)).map(_.toChar)

  private def randomFontName(): String = fonts(rnd.between(0, fonts.length))
  private def randomString(length: Int): String =
    List.fill(length)(charSet(rnd.between(0, charSet.length))).mkString

  performance of "Figlet4s" in {
    measure method "internalFonts" in {
      using(sizes) in { _ =>
        Figlet4s.internalFonts
      }
    }

    measure method "loadFontInternal" in {
      using(sizes) in { _ =>
        Try(Figlet4s.loadFontInternal(randomFontName()))
      }
    }

    measure method "loadFont" in {
      using(sizes) in { _ =>
        val fontPath = s"./fonts/${randomFontName()}.flf"
        Try(Figlet4s.loadFont(fontPath, "ISO8859_1"))
      }
    }

    measure method "renderString" in {
      val options = Figlet4s.builder().options
      using(sizes) in { length =>
        Figlet4s.renderString(randomString(length), options)
      }
    }
  }

}
