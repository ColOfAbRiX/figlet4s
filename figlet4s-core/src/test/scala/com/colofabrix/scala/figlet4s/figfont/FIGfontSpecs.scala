package com.colofabrix.scala.figlet4s.figfont

import cats.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import java.io.File

class FIGfontSpecs extends AnyFlatSpec with Matchers with ValidatedMatchers with ValidatedValues {

  "FIGfont creation" should "succeed when data is valid" in new FontScope {
    val computed = adaptError(FIGfont(  new File("test"), font.allLines().iterator))
    computed should be(valid)
  }

  // Required chars

  "FIGfont required chars" should "follow the FIGfont standard" in new FontScope {
    val requiredChars = ((32 to 126) ++ Seq(196, 214, 220, 223, 228, 246, 252)).map(_.toChar)
    FIGfont.requiredChars should equal(requiredChars)
  }

  // Input Iterator

  "The Input Iterator" should "fail if the data ends before the full file is read" in new FontScope {
    val iterator = font.allLines(false).take(150).iterator
    val computed = adaptError(FIGfont(  new File("test"), iterator))
    computed should be(invalid)
    computed.invalidValue.head should startWith("FIGcharacterError - Missing definition for required FIGlet characters:")
  }

  it should "fail if a line of the Iterator is missing" in new FontScope {
    val iterator = font.allLines(false).zipWithIndex.filter { case (_, i) => i != 150 }.map(_._1).iterator
    val computed = adaptError(FIGfont(  new File("test"), iterator))
    computed should haveInvalid("FIGcharacterError - Incomplete character definition at the end of the file")
  }

  // Header

  "Header validation" should "fail when the header is invalid" in new FontScope {
    val iterator = font.allLines(false).patch(0, Seq("adfadsa"), 1).iterator
    val computed = adaptError(FIGfont(  new File("test"), iterator))
    computed should haveInvalid("FIGheaderError - Wrong number of parameters in FLF header. Found 1 parameters")
  }

  // Comments

  "Comments validation" should "fail if the lines of comments are incorrect" in new FontScope {
    val iterator = font.copy(comment = "asdfad").allLines(false).iterator
    val computed = adaptError(FIGfont(  new File("test"), iterator))
    computed should haveInvalid("FIGcharacterError - Incomplete character definition at the end of the file")
  }

  // FIGcharacters

  "FIGcharacters validation" should "fail if required characters are missing" in new FontScope {
    val iterator = font
      .flatMapChars(false) { (char, i) =>
        if (i == 1) Vector.empty else Vector(char)
      }.iterator
    val computed = adaptError(FIGfont(  new File("test"), iterator))
    computed should be(invalid)
    computed.invalidValue.head should startWith("FIGcharacterError - Missing definition for required FIGlet")
  }

  it should "fail if a FIGcharacter is not valid" in new FontScope {
    val iterator = font
      .flatMapChars(false) { (char, i) =>
        if (i == 1) Vector(char.replace("@@", "")) else Vector(char)
      }.iterator
    val computed = adaptError(FIGfont(  new File("test"), iterator))
    computed should be(invalid)
    computed.invalidValue.head should startWith("FIGcharacterError - Can't determine endmark.")
  }

  it should "fail if the total number of characters doesn't respect the header" in new FontScope {
    val newHeader = header.copy(codetagCount = "2").toLine
    val iterator  = font.allLines().patch(0, Seq(newHeader), 1).iterator
    val computed  = adaptError(FIGfont( new File("test"), iterator))
    computed should haveInvalid(
      "FIGFontError - The number of loaded tagged fonts 4 doesn't correspond to the value indicated in the header 2",
    )
  }

  it should "fail if the name of a tag is missing" in new FontScope {
    val iterator = font.flatMapTagged { (char, i) =>
      if (i == 0)
        Vector(("  NO-BREAK SPACE" +: char.split("\n").tail).mkString("\n"))
      else
        Vector(char)
    }.iterator
    val computed = adaptError(FIGfont(  new File("test"), iterator))
    computed should be(invalid)
    computed.invalidValue.head should startWith("FIGcharacterError - Couldn't convert character code ''")
  }

  it should "fail if the name of a tag is an invalid value" in new FontScope {
    val iterator = font.flatMapTagged { (char, i) =>
      if (i == 0)
        Vector(("ABCD  NO-BREAK SPACE" +: char.split("\n").tail).mkString("\n"))
      else
        Vector(char)
    }.iterator
    val computed = adaptError(FIGfont(  new File("test"), iterator))
    computed should be(invalid)
    computed.invalidValue.head should startWith("FIGcharacterError - Couldn't convert character code 'ABCD'")
  }

  it should "not fail if the comment of a tag is missing" in new FontScope {
    val iterator = font.flatMapTagged { (char, i) =>
      if (i == 0)
        Vector(("160" +: char.split("\n").tail).mkString("\n"))
      else
        Vector(char)
    }.iterator
    val computed = adaptError(FIGfont(  new File("test"), iterator))
    computed should be(valid)
  }

}
