package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.unsafe._
import org.scalameter.api._

import org.scalameter.picklers.Implicits._

@SuppressWarnings(Array("org.wartremover.warts.All"))
abstract class FabReporter extends Bench.Local[Double] {
  import org.scalameter.reporting._
  import org.scalameter._

  override def persistor: Persistor =
    new persistence.GZIPJSONSerializationPersistor

  def aggregator: Aggregator[Double] =
    Aggregator.average

  def measurer: Measurer[Double] =
    new Measurer.IgnoringGC with Measurer.PeriodicReinstantiation[Double] with Measurer.OutlierElimination[Double]
    with Measurer.RelativeNoise {
      def numeric: Numeric[Double] = implicitly[Numeric[Double]]
    }

  override def reporter: Reporter[Double] =
    Reporter.Composite(
      new RegressionReporter(tester, historian),
      HtmlReporter(!online),
    )

  def tester: RegressionReporter.Tester =
    RegressionReporter.Tester.Accepter()

  def historian: RegressionReporter.Historian =
    RegressionReporter.Historian.ExponentialBackoff()

  def online: Boolean = false
}

final class Figlet4sUnsafeBenchmark extends FabReporter {

  val sizes: Gen[Int] = Gen.range("size")(1, 10000, 100)

  performance of "Figlet4sClient" in {

    measure method "internalFonts" in {
      using(sizes) in { _ =>
        Figlet4s.internalFonts.foreach(x => x + "")
      }
    }

  }

}
