package com.colofabrix.scala.figlet4s.figfont

import cats.data._
import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters.HorizontalLayout._
import com.colofabrix.scala.figlet4s.figfont.StandardFont._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters.{ HorizontalSmushingRule => HSR, _ }
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters.{
  FullLayout => FL, OldLayout => OL, PrintDirection => HPD,
}

class FIGfontParametersSpecs extends AnyFlatSpec with Matchers with ValidatedMatchers with ValidatedValues {

  "PrintDirection.fromHeader" should "return the correct values for every input value" in {
    val tests = Seq(
      Some(HPD.LeftToRight) -> PrintDirection.LeftToRight,
      Some(HPD.RightToLeft) -> PrintDirection.RightToLeft,
      None                  -> PrintDirection.LeftToRight,
    )

    // Making sure we are covering all input and all output values
    tests.map(_._1).toSet should have size 3
    tests.map(_._2).toSet should have size 2

    for ((headerValue, fontValue) <- tests) {
      val newHeader = header.toFIGheader.value.copy(printDirection = headerValue)
      val computed  = adaptError(PrintDirection.fromHeader(newHeader))
      computed should be(valid)
      computed.value should equal(fontValue)
    }
  }

  "HorizontalLayout.fromOldLayout" should "return the correct values for every input value" in {
    val tests = Seq(
      Vector(OL.FullWidth)              -> FullWidth,
      Vector(OL.HorizontalFitting)      -> HorizontalFitting,
      Vector(OL.EqualCharacterSmushing) -> ControlledSmushing(Vector(HSR.EqualCharacter)),
      Vector(OL.UnderscoreSmushing)     -> ControlledSmushing(Vector(HSR.Underscore)),
      Vector(OL.HierarchySmushing)      -> ControlledSmushing(Vector(HSR.Hierarchy)),
      Vector(OL.OppositePairSmushing)   -> ControlledSmushing(Vector(HSR.OppositePair)),
      Vector(OL.BigXSmushing)           -> ControlledSmushing(Vector(HSR.BigX)),
      Vector(OL.HardblankSmushing)      -> ControlledSmushing(Vector(HSR.Hardblank)),
    )

    // Making sure we are covering all input and all output values
    tests.map(_._1).toSet should have size 8
    tests.map(_._2).toSet should have size 8

    for ((headerValue, fontValue) <- tests) {
      val newHeader = header.toFIGheader.value.copy(oldLayout = headerValue)
      val computed  = adaptError(HorizontalLayout.fromOldLayout(newHeader))
      computed should be(valid)
      computed.value should equal(fontValue)
    }
  }

  it should "return multiple smushing rules in the output" in {
    val input     = Vector(OL.EqualCharacterSmushing, OL.UnderscoreSmushing)
    val newHeader = header.toFIGheader.value.copy(oldLayout = input)
    val computed  = adaptError(HorizontalLayout.fromOldLayout(newHeader))
    val expected  = ControlledSmushing(Vector(HSR.EqualCharacter, HSR.Underscore))
    computed should be(valid)
    computed.value should equal(expected)
  }

  it should "fail when given FullWidth + a smushing rule" in {
    val input     = Vector(OL.FullWidth, OL.UnderscoreSmushing)
    val newHeader = header.toFIGheader.value.copy(oldLayout = input)
    val computed  = adaptError(HorizontalLayout.fromOldLayout(newHeader))
    computed should haveInvalid(
      "FIGFontError - Couldn't convert layout settings found in header: FullWidth, UnderscoreSmushing",
    )
  }

  it should "fail when given HorizontalFitting + a smushing rule" in {
    val input     = Vector(OL.HorizontalFitting, OL.UnderscoreSmushing)
    val newHeader = header.toFIGheader.value.copy(oldLayout = input)
    val computed  = adaptError(HorizontalLayout.fromOldLayout(newHeader))
    computed should haveInvalid(
      "FIGFontError - Couldn't convert layout settings found in header: HorizontalFitting, UnderscoreSmushing",
    )
  }

  "HorizontalLayout.fromFullLayout" should "return the correct values for every input value" in {
    // format: off
    val tests = Seq(
      None                                                                     -> None,
      Some(Vector())                                                           -> Some(FullWidth),
      Some(Vector(FL.HorizontalFitting))                                       -> Some(HorizontalFitting),
      Some(Vector(FL.HorizontalSmushing))                                      -> Some(UniversalSmushing),
      Some(Vector(FL.HorizontalSmushing, FL.EqualCharacterHorizontalSmushing)) -> Some(ControlledSmushing(Vector(HSR.EqualCharacter))),
      Some(Vector(FL.HorizontalSmushing, FL.UnderscoreHorizontalSmushing))     -> Some(ControlledSmushing(Vector(HSR.Underscore))),
      Some(Vector(FL.HorizontalSmushing, FL.HierarchyHorizontalSmushing))      -> Some(ControlledSmushing(Vector(HSR.Hierarchy))),
      Some(Vector(FL.HorizontalSmushing, FL.OppositePairHorizontalSmushing))   -> Some(ControlledSmushing(Vector(HSR.OppositePair))),
      Some(Vector(FL.HorizontalSmushing, FL.BigXHorizontalSmushing))           -> Some(ControlledSmushing(Vector(HSR.BigX))),
      Some(Vector(FL.HorizontalSmushing, FL.HardblankHorizontalSmushing))      -> Some(ControlledSmushing(Vector(HSR.Hardblank))),
    )
    // format: on

    // Making sure we are covering all input and all output values
    tests.map(_._1).toSet should have size 10
    tests.map(_._2).toSet should have size 10

    for ((headerValue, fontValue) <- tests) {
      val newHeader = header.toFIGheader.value.copy(fullLayout = headerValue)
      val computed  = adaptError(HorizontalLayout.fromFullLayout(newHeader))
      computed should be(valid)
      computed.value should equal(fontValue)
    }
  }

  it should "return multiple smushing rules in the output" in {
    val input     = Vector(FL.HorizontalSmushing, FL.EqualCharacterHorizontalSmushing, FL.BigXHorizontalSmushing)
    val newHeader = header.toFIGheader.value.copy(fullLayout = Some(input))
    val computed  = adaptError(HorizontalLayout.fromFullLayout(newHeader))
    val expected  = Some(ControlledSmushing(Vector(HSR.EqualCharacter, HSR.BigX)))
    computed should be(valid)
    computed.value should equal(expected)
  }

  //  Support  //

  private def adaptError[A](value: FigletResult[A]): ValidatedNel[String, A] =
    value.leftMap { errors =>
      errors.toNonEmptyList.map(error => s"${error.getClass.getSimpleName} - ${error.getMessage}")
    }

}
