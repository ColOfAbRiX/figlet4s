package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Main extends App {
  private def unsafeTest() = {
    import com.colofabrix.scala.figlet4s.unsafe._

    val common = Figlet4s.builder("Fabrizio & Claire 123")

    //  First  //

    val first = common
      .withHorizontalLayout(
        ControlledHorizontalSmushingLayout(
          Vector(UnderscoreHorizontalSmushing, HierarchyHorizontalSmushing, OppositePairHorizontalSmushing),
        ),
      )
    println(first.options.horizontalLayout)

    first
      .render()
      .print()

    //  Second  //

    val second = common
      .withInternalFont("alligator")
      .withHorizontalLayout(
        ControlledHorizontalSmushingLayout(Vector(EqualCharacterHorizontalSmushing, HardblankHorizontalSmushing)),
      )
    println(second.options.horizontalLayout)

    second
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
