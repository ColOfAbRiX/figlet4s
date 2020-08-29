package com.colofabrix.scala.figlet4s

import org.scalameter.api._
import org.scalameter.picklers.Implicits._

@SuppressWarnings(Array("org.wartremover.warts.LeakingSealed"))
abstract class LocalHTMLReporter extends Bench[Double] {
  import org.scalameter.reporting._
  import org.scalameter._

  def executor: Executor[Double] =
    new execution.LocalExecutor(warmer, aggregator, measurer)

  private def warmer: Warmer =
    new Warmer.Default

  private def aggregator: Aggregator[Double] =
    Aggregator.average

  def measurer: Measurer[Double] =
    new Measurer.IgnoringGC with Measurer.PeriodicReinstantiation[Double] with Measurer.OutlierElimination[Double]
    with Measurer.RelativeNoise {
      def numeric: Numeric[Double] = implicitly[Numeric[Double]]
    }

  override def reporter: Reporter[Double] =
    Reporter.Composite(
      new RegressionReporter(tester, historian),
      HtmlReporter(true),
    )

  private def tester: RegressionReporter.Tester =
    RegressionReporter.Tester.Accepter()

  private def historian: RegressionReporter.Historian =
    RegressionReporter.Historian.ExponentialBackoff()

  override def persistor: Persistor =
    new persistence.GZIPJSONSerializationPersistor
}
