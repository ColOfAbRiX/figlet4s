package com.colofabrix.scala.figlet4s

//import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Main extends App {
  private def unsafeTest() = {
    import com.colofabrix.scala.figlet4s.unsafe._

    val builder1 = Figlet4s
      .builder("Fabrizio & Claire")
      .withInternalFont("4max")
    //println(s"Header: ${builder1.options.font.header}")
    //println(s"Options: ${builder1.options}")
    builder1
      .render()
      .print()

    val builder2 = Figlet4s
      .builder("Fabrizio & Claire")
      .withInternalFont("alligator2")
    //println(s"Header: ${builder2.options.font.header}")
    //println(s"Options: ${builder2.options}")
    builder2
      .render()
      .print()

    val builder3 = Figlet4s
      .builder("Fabrizio & Claire")
      .withInternalFont("standard")
    //println(s"Header: ${builder3.options.font.header}")
    //println(s"Options: ${builder3.options}")
    builder3
      .render()
      .print()
  }
  unsafeTest()

  //private def ioTest() = {
  //  import com.colofabrix.scala.figlet4s.catsio._
  //  val ioapp = for {
  //    figure <- Figlet4s
  //                .builder("Fa & Cl")
  //                .withInternalFont("standard")
  //                .render()
  //    _ <- figure.print()
  //  } yield ()
  //  ioapp.unsafeRunSync()
  //}
  //ioTest()
}
