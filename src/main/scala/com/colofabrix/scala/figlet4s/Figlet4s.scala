package com.colofabrix.scala.figlet4s

import scala.io.Source
import cats.effect.IOApp
import cats.effect.{ ExitCode, IO }
import com.colofabrix.scala.figlet4s.figfont._

object Figlet4s extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    IO {
      val lines = Source.fromResource("fonts/standard.flf").getLines().toVector
      val font  = FIGfont.fromLines(lines)
      pprint.pprintln(font)
    } as ExitCode.Success

}

// 15 = 8 + 2 + 1
// Vector(FullWidthLayoutOldlayout, KerningLayoutOldlayout, HorizontalSmushingRule1Oldlayout, HorizontalSmushingRule2Oldlayout),
// 24463
// Vector(HorizontalSmushingRule1NewLayout, HorizontalSmushingRule2NewLayout, HorizontalSmushingRule3NewLayout, HorizontalSmushingRule4NewLayout, HorizontalSmushingNewLayout, VerticalSmushingRule1NewLayout, VerticalSmushingRule2NewLayout, VerticalSmushingRule3NewLayout, VerticalSmushingRule4NewLayout, VerticalSmushingRule5NewLayout, VerticalSmushingNewLayout)),
