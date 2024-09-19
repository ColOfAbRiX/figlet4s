package com.colofabrix.scala.figlet4s

import org.scalameter.api._
import org.scalameter.picklers.Implicits._

abstract class FigletBenchmark extends Bench[Double] {
  import org.scalameter.reporting._
  import org.scalameter._

  def executor: Executor[Double] =
    new execution.SeparateJvmsExecutor(warmer, aggregator, measurer)

  private def warmer: Warmer =
    new Warmer.Default

  private def aggregator: Aggregator[Double] =
    Aggregator.average

  def measurer: Measurer[Double] =
    new Measurer.IgnoringGC with Measurer.PeriodicReinstantiation[Double] with Measurer.OutlierElimination[Double]
    with Measurer.RelativeNoise {
      def numeric: Numeric[Double] = implicitly[Numeric[Double]]
    }

  def persistor: Persistor =
    new persistence.GZIPJSONSerializationPersistor

  private def historian: RegressionReporter.Historian =
    RegressionReporter.Historian.ExponentialBackoff()

  private def tester: RegressionReporter.Tester =
    RegressionReporter.Tester.Accepter()

  override def reporter: Reporter[Double] =
    RegressionReporter(tester, historian)
}
