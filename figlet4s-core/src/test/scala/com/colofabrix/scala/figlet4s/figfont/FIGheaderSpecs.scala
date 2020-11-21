package com.colofabrix.scala.figlet4s.figfont

import cats.data._
import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont.StandardFont._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters.PrintDirection

class FIGheaderSpecs extends AnyFlatSpec with Matchers with ValidatedMatchers with ValidatedValues {

  //  signature  //

  "FIGheader.apply()" should "fail with wrong signature" in {
    val mistake  = toHeaderLine(header.updated("signature", "abcde"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Wrong FLF signature: abcde")
  }

  it should "return the correct signature" in {
    val lineHeader = toHeaderLine(header)
    val computed   = FIGheader(lineHeader).map(_.signature)
    computed should beValid(header("signature"))
  }

  //  hardblank  //

  it should "fail with wrong hardblank" in {
    val mistake  = toHeaderLine(header.updated("hardblank", "$$"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - The hardblank '$$' is not composed of only one character")
  }

  it should "return the correct hardblank" in {
    val lineHeader = toHeaderLine(header)
    val computed   = FIGheader(lineHeader).map(_.hardblank)
    computed should beValid(header("hardblank").charAt(0))
  }

  //  height  //

  it should "fail with a non-numeric height" in {
    val mistake  = toHeaderLine(header.updated("height", "abcd"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Couldn't parse header field 'height': abcd")
  }

  it should "fail with a negative height" in {
    val mistake  = toHeaderLine(header.updated("height", "-1"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Field 'height' must be positive: -1")
  }

  it should "return the correct height" in {
    val lineHeader = toHeaderLine(header)
    val computed   = FIGheader(lineHeader).map(_.height)
    computed should beValid(header("height").toInt)
  }

  //  baseline  //

  it should "fail with a non-numeric baseline" in {
    val mistake  = toHeaderLine(header.updated("baseline", "abcd"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Couldn't parse header field 'baseline': abcd")
  }

  it should "fail with a negative baseline" in {
    val mistake  = toHeaderLine(header.updated("baseline", "-1"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Field 'baseline' must be positive: -1")
  }

  it should "return the correct baseline" in {
    val lineHeader = toHeaderLine(header)
    val computed   = FIGheader(lineHeader).map(_.baseline)
    computed should beValid(header("baseline").toInt)
  }

  //  maxLength  //

  it should "fail with a non-numeric maxLength" in {
    val mistake  = toHeaderLine(header.updated("maxLength", "abcd"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Couldn't parse header field 'maxLength': abcd")
  }

  it should "fail with a negative maxLength" in {
    val mistake  = toHeaderLine(header.updated("maxLength", "-1"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Field 'maxLength' must be positive: -1")
  }

  it should "return the correct maxLength" in {
    val lineHeader = toHeaderLine(header)
    val computed   = FIGheader(lineHeader).map(_.maxLength)
    computed should beValid(header("maxLength").toInt)
  }

  //  oldLayout  //

  it should "fail with a non-numeric oldLayout" in {
    val mistake  = toHeaderLine(header.updated("oldLayout", "abcd"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Couldn't parse header field 'oldLayout': abcd")
  }

  it should "fail with an oldLayout greater than 63" in {
    val mistake  = toHeaderLine(header.updated("oldLayout", "64"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Field 'oldLayout' must be between -1 and 63, both included: 64")
  }

  it should "fail with an oldLayout less than -1" in {
    val mistake  = toHeaderLine(header.updated("oldLayout", "-2"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Field 'oldLayout' must be between -1 and 63, both included: -2")
  }

  it should "return the correct oldLayout" in {
    val expected = FIGheaderParameters.OldLayout(header("oldLayout").toInt)
    val computed = FIGheader(toHeaderLine(header)).map(_.oldLayout)
    computed should be(valid)
    computed.value should contain theSameElementsAs (expected)
  }

  //  commentLines  //

  it should "fail with a non-numeric commentLines" in {
    val mistake  = toHeaderLine(header.updated("commentLines", "abcd"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Couldn't parse header field 'commentLines': abcd")
  }

  it should "fail with a negative commentLines" in {
    val mistake  = toHeaderLine(header.updated("commentLines", "-1"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Field 'commentLines' must be non-negative: -1")
  }

  it should "return the correct commentLines" in {
    val lineHeader = toHeaderLine(header)
    val computed   = FIGheader(lineHeader).map(_.commentLines)
    computed should beValid(header("commentLines").toInt)
  }

  //  printDirection  //

  it should "fail with a non-numeric printDirection" in {
    val mistake  = toHeaderLine(header.updated("printDirection", "abcd"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Couldn't parse header field 'printDirection': Some(abcd)")
  }

  it should "fail with a negative printDirection" in {
    val mistake  = toHeaderLine(header.updated("printDirection", "-1"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Invalid value for field 'printDirection': -1")
  }

  it should "return the correct printDirection" in {
    val expected = PrintDirection(header("printDirection").toInt).map(Some(_))
    val computed = FIGheader(toHeaderLine(header)).map(_.printDirection)
    computed should be(valid)
    computed shouldBe expected
  }

  it should "return None when printDirection is not specified" in {
    val lineHeader = toHeaderLine(noPrintDirection(header))
    val computed   = FIGheader(lineHeader).map(_.printDirection)
    computed should be(valid)
    computed.value shouldBe None
  }

  //  fullLayout  //

  it should "fail with a non-numeric fullLayout" in {
    val mistake  = toHeaderLine(header.updated("fullLayout", "abcd"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Couldn't parse header field 'fullLayout': Some(abcd)")
  }

  it should "fail with an fullLayout greater than 32767" in {
    val mistake  = toHeaderLine(header.updated("fullLayout", "32768"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Field 'fullLayout' must be between 0 and 32767, both included: 32768")
  }

  it should "fail with an fullLayout less than 0" in {
    val mistake  = toHeaderLine(header.updated("fullLayout", "-1"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Field 'fullLayout' must be between 0 and 32767, both included: -1")
  }

  it should "return the correct fullLayout when it's specified" in {
    val expected = FIGheaderParameters.FullLayout(header("fullLayout").toInt)
    val lineHeader = toHeaderLine(header)
    val computed   = FIGheader(lineHeader).map(_.fullLayout)
    computed should be(valid)
    computed.value.getOrElse(Vector.empty) should contain theSameElementsAs (expected)
  }

  it should "return None when fullLayout is not specified" in {
    val lineHeader = toHeaderLine(noFullLayout(header))
    val computed   = FIGheader(lineHeader).map(_.fullLayout)
    computed should be(valid)
    computed.value shouldBe None
  }

  //  codetagCount  //

  it should "fail with a non-numeric codetagCount" in {
    val mistake  = toHeaderLine(header.updated("codetagCount", "abcd"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Couldn't parse header field 'codetagCount': Some(abcd)")
  }

  it should "fail with a negative codetagCount" in {
    val mistake  = toHeaderLine(header.updated("codetagCount", "-1"))
    val computed = adaptError(FIGheader(mistake))
    computed should haveInvalid("FIGheaderError - Field 'codetagCount' must be non-negative: Some(-1)")
  }

  it should "return the correct codetagCount when it's specified" in {
    val lineHeader = toHeaderLine(header)
    val computed   = FIGheader(lineHeader).map(_.codetagCount)
    computed should be(valid)
    computed.value shouldBe Some(header("codetagCount").toInt)
  }

  it should "return None when codetagCount is not specified" in {
    val lineHeader = toHeaderLine(noCodetagCount(header))
    val computed   = FIGheader(lineHeader).map(_.codetagCount)
    computed should be(valid)
    computed.value shouldBe None
  }

  "FIGheader.singleLine()" should "return the same header that constructed it" in {
    val expected = toHeaderLine(header)
    val computed = FIGheader(expected).value.singleLine()
    expected should equal(computed)
  }

  //  Support  //

  private def adaptError[A](value: FigletResult[A]): ValidatedNel[String, A] =
    value.leftMap { errors =>
      errors.toNonEmptyList.map(error => s"${error.getClass.getSimpleName} - ${error.getMessage}")
    }

}
