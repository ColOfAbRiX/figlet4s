package com.colofabrix.scala.figlet4s.testutils.originalfiglet

import cats.effect._
import cats.implicits._
import org.scalacheck._
import org.scalacheck.effect.PropF

/** Runs the property checks for a single rendering configuration, collecting the outcome instead of failing */
object OriginalFigletProperties {

  private val MinSuccessfulTests =
    5

  def run(
      testData: TestRenderOptions,
  )(f: TestRenderOptions => IO[Unit]): IO[(TestRenderOptions, Either[Throwable, Unit])] = {
    val generator =
      OriginalFigletTexts.generator.map { text =>
        testData.copy(renderText = text)
      }

    PropF
      .forAllF(generator) { (sample: TestRenderOptions) =>
        f(sample)
      }
      .check {
        Test.Parameters.default.withMinSuccessfulTests(MinSuccessfulTests)
      }
      .flatMap(checkResult)
      .attempt
      .tupleLeft(testData)
  }

  private def checkResult(result: Test.Result): IO[Unit] =
    result.status match {
      case Test.Passed | _: Test.Proved =>
        IO.unit
      case Test.PropException(_, e, _) =>
        IO.raiseError(e)
      case Test.Failed(args, labels) =>
        val niceArgs = args.map(_.arg).mkString(", ")
        IO.raiseError(new AssertionError(s"Property falsified for [$niceArgs] $labels"))
      case other =>
        IO.raiseError(new AssertionError(s"Property was not satisfied: $other"))
    }

}
