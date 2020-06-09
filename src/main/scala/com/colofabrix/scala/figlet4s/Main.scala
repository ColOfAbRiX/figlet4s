package com.colofabrix.scala.figlet4s

import _root_.cats.data.Validated._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Main extends App {
  val renderedV = Figlet4s.renderString("Fabrizio & Claire")
  renderedV match {
    case Valid(rendered) =>
      // println(rendered.lines)
      rendered.lines.foreach(_.foreach(println))
    case Invalid(err) => pprint.pprintln(err)
  }

  // for (font <- Figlet4s.availableFonts) {
  //   println(s"Font: $font")
  //   val string = Figlet4s.loadString("Fabrizio & Claire", font)
  //   string match {
  //     case Invalid(err)  => pprint.pprintln(err)
  //     case Valid(string) => string.render().foreach(println)
  //   }
  //   println("")
  // }
}
