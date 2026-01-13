package com.colofabrix.scala.figlet4s.figfont

import cats.data._
import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class FIGheaderParametersSpecs extends AnyFlatSpec with Matchers with ValidatedMatchers with ValidatedValues {

  //  PrintDirection  //

  trait PrintDirection {

    val goodValues: List[Int] = List(0, 1)
    val badValues: List[Int]  = List(-2, -1, 2, 3)

  }

  "PrintDirection" should "create a valid PrintDirection" in new PrintDirection {
    val computed = goodValues.traverse(PrintDirection(_))
    computed should be(valid)
    computed.value should have length (goodValues.length.toLong)
    computed.value.toSet should contain theSameElementsAs computed.value
  }

  it should "return an error when providing a wrong value" in new PrintDirection {
    val computed = adaptError(badValues.traverse(PrintDirection(_)))
    computed should be(invalid)
    computed.invalidValue should have length (badValues.length.toLong)
  }

  it should "have its primitive values in the FLF header range" in new PrintDirection {
    PrintDirection.values.map(_.value) should contain theSameElementsAs goodValues
  }

  //  OldLayout  //

  trait OldLayoutScope {

    val goodValues: List[Int] = List(-1, 0, 1, 2, 4, 8, 16, 32)
    val badValues: List[Int]  = List(-3, -2, 128, 192)
    val allValues: Int        = goodValues.filter(_ > 0).sum

  }

  "OldLayout" should "create a valid OldLayout" in new OldLayoutScope {
    val computed = goodValues.traverse(OldLayout(_)).map(_.flatten)
    computed should be(valid)
    computed.value should have length (goodValues.length.toLong)
    computed.value.toSet should contain theSameElementsAs computed.value
  }

  it should "return an error when providing at least one wrong value" in new OldLayoutScope {
    val computed = adaptError(badValues.traverse(OldLayout(_)))
    computed should be(invalid)
    computed.invalidValue should have length (badValues.length.toLong)
  }

  it should "have its primitive values in the FLF header range" in new OldLayoutScope {
    OldLayout.values.map(_.value) should contain theSameElementsAs goodValues
  }

  it should "factor out binary digits and create multiple valid OldLayout" in new OldLayoutScope {
    val computed = OldLayout(3)
    val expected = List(OldLayout.EqualCharacterSmushing, OldLayout.UnderscoreSmushing)
    computed should be(valid)
    computed.value should contain theSameElementsAs expected
  }

  it should "return all values (with FullWidth and HorizontalFitting being separate values)" in new OldLayoutScope {
    val computed = (OldLayout(-1), OldLayout(0), OldLayout(allValues)).mapN(_ ++ _ ++ _)
    val expected = OldLayout.values
    computed should be(valid)
    computed.value should contain theSameElementsAs expected
  }

  //  FullLayout  //

  trait FullLayoutScope {

    val goodValues: List[Int] = List(1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384)
    val badValues: List[Int]  = List(-2, -1, 32768, 32769)
    val allValues: Int        = goodValues.filter(_ > 0).sum

  }

  "FullLayout" should "create a valid FullLayout" in new FullLayoutScope {
    val computed = goodValues.traverse(FullLayout(_)).map(_.flatten)
    computed should be(valid)
    computed.value should have length (goodValues.length.toLong)
    computed.value.toSet should contain theSameElementsAs computed.value
  }

  it should "return an error when providing at least one wrong value" in new FullLayoutScope {
    val computed = adaptError(badValues.traverse(FullLayout(_)))
    computed should be(invalid)
    computed.invalidValue should have length (badValues.length.toLong)
  }

  it should "have its primitive values in the FLF header range" in new FullLayoutScope {
    FullLayout.values.map(_.value) should contain theSameElementsAs goodValues
  }

  it should "return the list of all horizontal layouts" in {
    FullLayout.horizontalSmushingRules should have length (6)
  }

  it should "return the list of all vertical layouts" in {
    FullLayout.verticalSmushingRules should have length (5)
  }

  it should "factor out binary digits and create multiple valid FullLayout" in new FullLayoutScope {
    val computed = FullLayout(3)
    val expected = List(FullLayout.EqualCharacterHorizontalSmushing, FullLayout.UnderscoreHorizontalSmushing)
    computed should be(valid)
    computed.value should contain theSameElementsAs expected
  }

  it should "return all values" in new FullLayoutScope {
    val computed = FullLayout(allValues)
    val expected = FullLayout.values
    computed should be(valid)
    computed.value should contain theSameElementsAs expected
  }

  it should "return an empty result for the input 0" in new FullLayoutScope {
    val computed = FullLayout(0)
    computed should be(valid)
    computed.value should be(empty)
  }

  //  Support  //

  private def adaptError[A](value: FigletResult[A]): ValidatedNel[String, A] =
    value.leftMap { errors =>
      errors.toNonEmptyList.map(error => s"${error.getClass.getSimpleName} - ${error.getMessage}")
    }

}
