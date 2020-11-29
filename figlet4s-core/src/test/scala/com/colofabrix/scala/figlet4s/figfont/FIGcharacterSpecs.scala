package com.colofabrix.scala.figlet4s.figfont

import cats.data._
import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont.StandardFont._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class FIGcharacterSpecs extends AnyFlatSpec with Matchers with ValidatedMatchers with ValidatedValues {

  "FIGcharacter creation" should "succeed when name is valid" in {
    val computed = adaptError(getFIGcharacter(' '))
    computed should be(valid)
  }

  //  Name  //

  "Name validation" should "fail when given a name = -1" in {
    val computed = adaptError(getFIGcharacter('\uffff'))
    computed should haveInvalid("FIGcharacterError - Name '-1' is illegal")
  }

  //  Endmark  //

  "Endmark validation" should "discover the endmark character" in {
    val computed = getFIGcharacter('a').value.endmark
    computed should equal('@')
  }

  it should "fail when endmarks are missing from the lines" in {
    val lines = getLinesAndBind("036") { (line, i) =>
      if (i == 1) Vector(line.replaceAll("@$", "")) else Vector(line)
    }
    val computed = adaptError(getFIGcharacter('a', lines))
    computed should haveInvalid(
      "FIGcharacterError - Can't determine endmark. There are lines with no termination or more than 2-characters " +
      "termination on character 'a' defined at line 124: (@, , @, @, @, @@)",
    )
  }

  it should "fail when lines terminate with more than two characters" in {
    val lines = getLinesAndBind("036") { (line, i) =>
      if (i == 1) Vector(line.replaceAll("@$", "@@@")) else Vector(line)
    }
    val computed = adaptError(getFIGcharacter('a', lines))
    computed should be(invalid)
    computed.invalidValue.head should startWith(
      "FIGcharacterError - Lines for character 'a' defined at line 124 are of different width:",
    )
  }

  it should "fail when there are different types of endmarks" in {
    val lines = getLinesAndBind("036") { (line, i) =>
      val newEndmark = List.fill(i % 2 + 1)(('a' + i).toChar).mkString
      Vector(line.replaceAll("""(.)\1?$""", newEndmark))
    }
    val computed = adaptError(getFIGcharacter('a', lines))
    computed should haveInvalid(
      "FIGcharacterError - Multiple endmarks found for character 'a' defined at line 124, only one endmark character " +
      "is allowed: (a, bb, c, dd, e, ff)",
    )
  }

  it should "succeed when a sub-character is the same as the 2-endmark" in {
    val lines = getLinesAndBind("036") { (line, _) =>
      Vector(line.replaceAll("@+$", "@@@"))
    }
    val computed = getFIGcharacter('a', lines)
    computed should be(valid)
  }

  it should "remove all consecutive endmarks from each line" in {
    val lines    = getFIGcharacter('a').value.lines.value
    val computed = lines.filter(_.matches("""@{1,2}$"""))
    computed should be(empty)
  }

  //  Max Width  //

  "Max Width validation" should "fail when the max width is negative" in {
    val computed = adaptError(getFIGcharacter('a', maxWidth = -1))
    computed should haveInvalid("FIGheaderError - Field 'maxLength' must be positive: -1")
  }

  it should "fail if the lines are not all of the same width" in {
    val lines = getLinesAndBind("036") { (line, i) =>
      Vector("a" * (i % 2) + line)
    }
    val computed = adaptError(getFIGcharacter('a', lines))
    computed should be(invalid)
    computed.invalidValue.head should startWith(
      "FIGcharacterError - Lines for character 'a' defined at line 124 are of different width",
    )
  }

  it should "fail if the width of the lines exceed max width" in {
    val lines = getLinesAndBind("036") { (line, _) =>
      Vector("a" * header.maxLength.toInt + line)
    }
    val computed = adaptError(getFIGcharacter('a', lines))
    computed should be(invalid)
    computed.invalidValue.head should startWith("FIGcharacterError - Maximum character width exceeded at line 124")
  }

  //  Height  //

  "Height validation" should "fail when the height is negative" in {
    val computed = adaptError(getFIGcharacter('a', height = -1))
    computed should haveInvalid("FIGheaderError - Field 'height' must be positive: -1")
  }

  it should "fail when the character height is less than the height parameter" in {
    val lines = getLinesAndBind("036") { (line, i) =>
      if (i == 0) Vector.empty else Vector(line)
    }
    val computed = adaptError(getFIGcharacter('a', lines))
    computed should be(invalid)
    computed.invalidValue.head should startWith(
      "FIGcharacterError - The character 'a' defined at line 124 doesn't respect the specified height of 6",
    )
  }

  it should "fail when the character height is more than the height parameter" in {
    val lines = getLinesAndBind("036") { (line, i) =>
      if (i == 0) Vector(line, line) else Vector(line)
    }
    val computed = adaptError(getFIGcharacter('a', lines))
    computed should haveInvalid(
      "FIGcharacterError - The character 'a' defined at line 124 doesn't respect the specified height of 6",
    )
  }

  //  Support  //

  private def adaptError[A](value: FigletResult[A]): ValidatedNel[String, A] =
    value.leftMap { errors =>
      errors.toNonEmptyList.map(error => s"${error.getClass.getSimpleName} - ${error.getMessage}")
    }

}
