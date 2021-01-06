package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalameter.api._

// Run on SBT with figlet4sBenchmarks/test
final class Figlet4sUnsafeBenchmark extends FigletBenchmark {

  private val maxLength     = 1000
  private val textLengthGen = Gen.range("text_length")(1, maxLength, 50)
  private val standardFont  = Figlet4s.loadFontInternal("standard")

  private val opts = Context(
    exec.benchRuns -> 25,
    exec.jvmflags  -> List("-Xms2G", "-Xmx2G"),
    verbose        -> true,
  )

  private val charSet = standardFont.characters.keySet.toVector
  private val rnd     = new scala.util.Random(System.currentTimeMillis())

  performance of "Figlet4s" in {
    def randomString(length: Int): String =
      List.fill(length)(charSet(rnd.nextInt(charSet.size))).mkString

    def testSet(options: RenderOptions): Gen[(String, RenderOptions)] =
      for (length <- textLengthGen) yield {
        (randomString(length), options)
      }

    // I'm interested in comparing Vector vs List for the rendering of text
    measure method "renderString" config opts in {
      performance of "full_line_width" in {
        val options = Figlet4s
          .builder()
          .withFont(standardFont)
          .withMaxWidth(maxLength * standardFont.header.maxLength)
          .options

        using(testSet(options)) in { data =>
          (Figlet4s.renderString _).tupled(data)
        }
      }

      performance of "short_line_width" in {
        val options = Figlet4s
          .builder()
          .withFont(standardFont)
          .withMaxWidth(120)
          .options

        using(testSet(options)) in { data =>
          (Figlet4s.renderString _).tupled(data)
        }
      }
    }
  }
}
