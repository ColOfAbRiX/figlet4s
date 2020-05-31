package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.figfont._
import scala.io.Source

object Figlet4s extends App {
  val lines = Source.fromResource("fonts/standard.flf").getLines().toVector
  val font  = FIGfont(lines.take(624))
  pprint.pprintln(font)
}

// 15 = 8 + 2 + 1
// Vector(FullWidthLayoutOldlayout, KerningLayoutOldlayout, HorizontalSmushingRule1Oldlayout, HorizontalSmushingRule2Oldlayout),
// 24463
// Vector(HorizontalSmushingRule1NewLayout, HorizontalSmushingRule2NewLayout, HorizontalSmushingRule3NewLayout, HorizontalSmushingRule4NewLayout, HorizontalSmushingNewLayout, VerticalSmushingRule1NewLayout, VerticalSmushingRule2NewLayout, VerticalSmushingRule3NewLayout, VerticalSmushingRule4NewLayout, VerticalSmushingRule5NewLayout, VerticalSmushingNewLayout)),
