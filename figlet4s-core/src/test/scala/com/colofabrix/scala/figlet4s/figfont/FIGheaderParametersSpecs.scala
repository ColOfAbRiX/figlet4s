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

   private val goodPrintDirection: List[Int] = List(0, 1)
   private val badPrintDirection: List[Int]  = List(-2, -1, 2, 3)

  "PrintDirection" should "create a valid PrintDirection" in {
    val computed = goodPrintDirection.traverse(PrintDirection(_))
    computed should be(valid)
    computed.value should have length goodPrintDirection.length.toLong
    computed.value.toSet should contain theSameElementsAs computed.value
  }

  it should "return an error when providing a wrong value" in {
    val computed = adaptError(badPrintDirection.traverse(PrintDirection(_)))
    computed should be(invalid)
    computed.invalidValue should have length badPrintDirection.length.toLong
  }

  it should "have its primitive values in the FLF header range" in {
    PrintDirection.values.map(_.value) should contain theSameElementsAs goodPrintDirection
  }

  //  OldLayout  //

  private val goodOldLayout: List[Int] = List(-1, 0, 1, 2, 4, 8, 16, 32)
  private val badOldLayout: List[Int]  = List(-3, -2, 128, 192)
  private val allOldLayout: Int        = goodOldLayout.filter(_ > 0).sum

  "OldLayout" should "create a valid OldLayout" in {
    val computed = goodOldLayout.traverse(OldLayout(_)).map(_.flatten)
    computed should be(valid)
    computed.value should have length goodOldLayout.length.toLong
    computed.value.toSet should contain theSameElementsAs computed.value
  }

  it should "return an error when providing at least one wrong value" in {
    val computed = adaptError(badOldLayout.traverse(OldLayout(_)))
    computed should be(invalid)
    computed.invalidValue should have length badOldLayout.length.toLong
  }

  it should "have its primitive values in the FLF header range" in {
    OldLayout.values.map(_.value) should contain theSameElementsAs goodOldLayout
  }

  it should "factor out binary digits and create multiple valid OldLayout" in {
    val computed = OldLayout(3)
    val expected = List(OldLayout.EqualCharacterSmushing, OldLayout.UnderscoreSmushing)
    computed should be(valid)
    computed.value should contain theSameElementsAs expected
  }

  it should "return all values (with FullWidth and HorizontalFitting being separate values)" in {
    val computed = (OldLayout(-1), OldLayout(0), OldLayout(allOldLayout)).mapN(_ ++ _ ++ _)
    val expected = OldLayout.values
    computed should be(valid)
    computed.value should contain theSameElementsAs expected
  }

  //  FullLayout  //

  private  val goodFullLayout: List[Int] = List(1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384)
  private  val badFullLayout: List[Int]  = List(-2, -1, 32768, 32769)
  private  val allFullLayout: Int        = goodFullLayout.filter(_ > 0).sum

  "FullLayout" should "create a valid FullLayout" in{
    val computed = goodFullLayout.traverse(FullLayout(_)).map(_.flatten)
    computed should be(valid)
    computed.value should have length goodFullLayout.length.toLong
    computed.value.toSet should contain theSameElementsAs computed.value
  }

  it should "return an error when providing at least one wrong value" in{
    val computed = adaptError(badFullLayout.traverse(FullLayout(_)))
    computed should be(invalid)
    computed.invalidValue should have length badFullLayout.length.toLong
  }

  it should "have its primitive values in the FLF header range" in{
    FullLayout.values.map(_.value) should contain theSameElementsAs goodFullLayout
  }

  it should "return the list of all horizontal layouts" in {
    FullLayout.horizontalSmushingRules should have length 6
  }

  it should "return the list of all vertical layouts" in {
    FullLayout.verticalSmushingRules should have length 5
  }

  it should "factor out binary digits and create multiple valid FullLayout" in{
    val computed = FullLayout(3)
    val expected = List(FullLayout.EqualCharacterHorizontalSmushing, FullLayout.UnderscoreHorizontalSmushing)
    computed should be(valid)
    computed.value should contain theSameElementsAs expected
  }

  it should "return all values" in{
    val computed = FullLayout(allFullLayout)
    val expected = FullLayout.values
    computed should be(valid)
    computed.value should contain theSameElementsAs expected
  }

  it should "return an empty result for the input 0" in{
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
