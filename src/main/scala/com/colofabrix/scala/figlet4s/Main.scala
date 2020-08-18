package com.colofabrix.scala.figlet4s

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Main extends App {
  private def unsafeTest() = {
    import com.colofabrix.scala.figlet4s.unsafe._

    Figlet4s
      .builder("~ Fabrizio & Claire *")
      .withInternalFont("4max")
      .render()
      .print()
    println("")
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
