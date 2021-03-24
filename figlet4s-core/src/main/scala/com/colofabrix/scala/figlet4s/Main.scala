package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.unsafe._

object Main extends App {

  println("Printing internal fonts")
  Figlet4s.internalFonts.foreach(println)

}
