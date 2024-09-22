package com.colofabrix.scala.figlet4s.figfont

import cats.scalatest._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters.{
  HorizontalLayout => HL, HorizontalSmushingRule => HSR, VerticalLayout => VL, VerticalSmushingRules => VSR, _
}
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters.{
  FullLayout => FL, OldLayout => OL, PrintDirection => HPD,
}
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class FIGfontParametersSpecs extends AnyFlatSpec with Matchers with ValidatedMatchers with ValidatedValues with SpecOps {

  "PrintDirection.fromHeader" should "return the correct values for every input value" in {
    val tests = Seq(
      // Header's print direction -> Font's print direction
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
      // Header's old layout            -> Font's horizontal layout
      Vector(OL.FullWidth)              -> HL.FullWidth,
      Vector(OL.HorizontalFitting)      -> HL.HorizontalFitting,
      Vector(OL.EqualCharacterSmushing) -> HL.ControlledSmushing(Vector(HSR.EqualCharacter)),
      Vector(OL.UnderscoreSmushing)     -> HL.ControlledSmushing(Vector(HSR.Underscore)),
      Vector(OL.HierarchySmushing)      -> HL.ControlledSmushing(Vector(HSR.Hierarchy)),
      Vector(OL.OppositePairSmushing)   -> HL.ControlledSmushing(Vector(HSR.OppositePair)),
      Vector(OL.BigXSmushing)           -> HL.ControlledSmushing(Vector(HSR.BigX)),
      Vector(OL.HardblankSmushing)      -> HL.ControlledSmushing(Vector(HSR.Hardblank)),
    )

    // Making sure we are covering all input and all output values
    tests.map(_._1).toSet should have size 8
    tests.map(_._2).toSet should have size 8

    for ((headerValue, fontValue) <- tests) {
      val newHeader = header.toFIGheader.value.copy(oldLayout = headerValue)
      val computed  = adaptError(HL.fromOldLayout(newHeader))
      computed should be(valid)
      computed.value should equal(fontValue)
    }
  }

  it should "return multiple smushing rules in the output" in {
    val input     = Vector(OL.EqualCharacterSmushing, OL.UnderscoreSmushing)
    val newHeader = header.toFIGheader.value.copy(oldLayout = input)
    val computed  = adaptError(HL.fromOldLayout(newHeader))
    val expected  = HL.ControlledSmushing(Vector(HSR.EqualCharacter, HSR.Underscore))
    computed should be(valid)
    computed.value should equal(expected)
  }

  it should "fail when given FullWidth + a smushing rule" in {
    val input     = Vector(OL.FullWidth, OL.UnderscoreSmushing)
    val newHeader = header.toFIGheader.value.copy(oldLayout = input)
    val computed  = adaptError(HL.fromOldLayout(newHeader))
    computed should haveInvalid(
      "FIGFontError - Couldn't convert layout settings found in header: FullWidth, UnderscoreSmushing",
    )
  }

  it should "fail when given HorizontalFitting + a smushing rule" in {
    val input     = Vector(OL.HorizontalFitting, OL.UnderscoreSmushing)
    val newHeader = header.toFIGheader.value.copy(oldLayout = input)
    val computed  = adaptError(HL.fromOldLayout(newHeader))
    computed should haveInvalid(
      "FIGFontError - Couldn't convert layout settings found in header: HorizontalFitting, UnderscoreSmushing",
    )
  }

  "HorizontalLayout.fromFullLayout" should "return the correct values for every input value" in {
    // format: off
    val tests = Seq(
      // Header's full layout                                                  -> Font's horizontal layout
      None                                                                     -> None,
      Some(Vector())                                                           -> Some(HL.FullWidth),
      Some(Vector(FL.HorizontalFitting))                                       -> Some(HL.HorizontalFitting),
      Some(Vector(FL.HorizontalSmushing))                                      -> Some(HL.UniversalSmushing),
      Some(Vector(FL.HorizontalSmushing, FL.EqualCharacterHorizontalSmushing)) -> Some(HL.ControlledSmushing(Vector(HSR.EqualCharacter))),
      Some(Vector(FL.HorizontalSmushing, FL.UnderscoreHorizontalSmushing))     -> Some(HL.ControlledSmushing(Vector(HSR.Underscore))),
      Some(Vector(FL.HorizontalSmushing, FL.HierarchyHorizontalSmushing))      -> Some(HL.ControlledSmushing(Vector(HSR.Hierarchy))),
      Some(Vector(FL.HorizontalSmushing, FL.OppositePairHorizontalSmushing))   -> Some(HL.ControlledSmushing(Vector(HSR.OppositePair))),
      Some(Vector(FL.HorizontalSmushing, FL.BigXHorizontalSmushing))           -> Some(HL.ControlledSmushing(Vector(HSR.BigX))),
      Some(Vector(FL.HorizontalSmushing, FL.HardblankHorizontalSmushing))      -> Some(HL.ControlledSmushing(Vector(HSR.Hardblank))),
    )
    // format: on

    // Making sure we are covering all input and all output values
    tests.map(_._1).toSet should have size 10
    tests.map(_._2).toSet should have size 10

    for ((headerValue, fontValue) <- tests) {
      val newHeader = header.toFIGheader.value.copy(fullLayout = headerValue)
      val computed  = adaptError(HL.fromFullLayout(newHeader))
      computed should be(valid)
      computed.value should equal(fontValue)
    }
  }

  it should "return multiple smushing rules in the output" in {
    val input     = Vector(FL.HorizontalSmushing, FL.EqualCharacterHorizontalSmushing, FL.BigXHorizontalSmushing)
    val newHeader = header.toFIGheader.value.copy(fullLayout = Some(input))
    val computed  = adaptError(HL.fromFullLayout(newHeader))
    val expected  = Some(HL.ControlledSmushing(Vector(HSR.EqualCharacter, HSR.BigX)))
    computed should be(valid)
    computed.value should equal(expected)
  }

  "HorizontalLayout.fromHeader" should "values from fullLayout when present" in {
    val newHeader = header
      .toFIGheader.value.copy(
        oldLayout = Vector(OL.FullWidth),
        fullLayout = Some(Vector(FL.HorizontalFitting)),
      )
    val computed = adaptError(HL.fromHeader(newHeader))
    val expected = HL.HorizontalFitting
    computed should be(valid)
    computed.value should equal(expected)
  }

  it should "return values from oldLayout when fullLayout not present" in {
    val newHeader = header
      .toFIGheader.value.copy(
        oldLayout = Vector(OL.FullWidth),
        fullLayout = None,
      )
    val computed = adaptError(HL.fromHeader(newHeader))
    val expected = HL.FullWidth
    computed should be(valid)
    computed.value should equal(expected)
  }

  "VerticalLayout.fromHeader" should "return the correct values for every input value" in {
    // format: off
    val tests = Seq(
      // Header's full layout                                              -> Font's vertical layout
      None                                                                 -> VL.FullHeight,
      Some(Vector())                                                       -> VL.FullHeight,
      Some(Vector(FL.VerticalFitting))                                     -> VL.VerticalFitting,
      Some(Vector(FL.VerticalSmushing))                                    -> VL.UniversalSmushing,
      Some(Vector(FL.VerticalSmushing, FL.EqualCharacterVerticalSmushing)) -> VL.ControlledSmushing(Vector(VSR.EqualCharacter)),
      Some(Vector(FL.VerticalSmushing, FL.UnderscoreVerticalSmushing))     -> VL.ControlledSmushing(Vector(VSR.Underscore)),
      Some(Vector(FL.VerticalSmushing, FL.HierarchyVerticalSmushing))      -> VL.ControlledSmushing(Vector(VSR.Hierarchy)),
      Some(Vector(FL.VerticalSmushing, FL.HorizontalLineVerticalSmushing)) -> VL.ControlledSmushing(Vector(VSR.HorizontalLine)),
      Some(Vector(FL.VerticalSmushing, FL.VerticalLineSupersmushing))      -> VL.ControlledSmushing(Vector(VSR.VerticalLineSupersmushing)),
    )
    // format: on

    // Making sure we are covering all input and all output values
    tests.map(_._1).toSet should have size 9
    tests.map(_._2).toSet should have size 8

    for ((headerValue, fontValue) <- tests) {
      val newHeader = header.toFIGheader.value.copy(fullLayout = headerValue)
      val computed  = adaptError(VL.fromHeader(newHeader))
      computed should be(valid)
      computed.value should equal(fontValue)
    }
  }

  it should "return multiple smushing rules in the output" in {
    val input     = Vector(FL.VerticalSmushing, FL.UnderscoreVerticalSmushing, FL.HierarchyVerticalSmushing)
    val newHeader = header.toFIGheader.value.copy(fullLayout = Some(input))
    val computed  = adaptError(VL.fromHeader(newHeader))
    val expected  = VL.ControlledSmushing(Vector(VSR.Underscore, VSR.Hierarchy))
    computed should be(valid)
    computed.value should equal(expected)
  }

}
