package com.colofabrix.scala.figlet4s.testutils

import cats.effect._
import cats.effect.implicits.concurrentParTraverseOps
import cats.effect.unsafe.implicits.global
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.testutils.originalfiglet._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest._

/**
 * Support for testing using the command line, original figlet executable.
 */
trait OriginalFigletTesting extends Notifying with Assertions with Checkpoints {

  private val Parallelism =
    Runtime.getRuntime.availableProcessors

  //  API  //

  /**
   * Renders a text with the given options using the figlet executable found on command line.
   */
  def renderWithFiglet(options: RenderOptions, text: String): IO[FIGure] =
    OriginalFigletCli.render(options, text)

  /**
   * Checks if an executable is present in the path and cancels the execution of the test if not present
   */
  def assumeExecutableInPath(executable: String): Unit =
    if (!OriginalFigletCli.executableExists(executable)) {
      Assertions.cancel(s"Executable $executable doesn't exist. Install $executable to run this test.")
    }

  /**
   * Returns the FIGfont with the given name, parsing it once and caching it
   */
  def cachedFont(fontName: String): FIGfont =
    OriginalFigletFonts.cached(fontName)

  /**
   * Runs property testing on a given function to test Figlet4s
   */
  def figletRenderingTest(f: TestRenderOptions => IO[Unit]): Unit = {
    val parallelTests =
      for {
        _              <- Vector(assumeExecutableInPath("figlet"))
        fontName       <- Figlet4s.internalFonts
        hLayout        <- HorizontalLayout.values.filterNot(_ == HorizontalLayout.ForceHorizontalSmushing)
        printDirection <- PrintDirection.values
        justification  <- Justification.values
      } yield {
        TestRenderOptions("", fontName, hLayout, printDirection, justification)
      }

    note(s"Figlet test cases: ${parallelTests.size}")

    val checkpoint = new Checkpoint()

    parallelTests
      .filterNot(KnownFigletIssues.isDisabled)
      .take(parallelTests.size) // TEMPORARY debugging accelerator, remove when the suite is stable
      .parTraverseN(Parallelism) { testData =>
        OriginalFigletProperties.run(testData)(f)
      }
      .unsafeRunSync()
      .foreach {
        case (testData, Left(e)) =>
          checkpoint {
            withClue(s"Problematic configuration $testData") {
              fail(e.getMessage)
            }
          }
        case (testData, Right(_)) =>
          note(s"Verified configuration $testData")
      }

    checkpoint.reportAll()
  }

}
