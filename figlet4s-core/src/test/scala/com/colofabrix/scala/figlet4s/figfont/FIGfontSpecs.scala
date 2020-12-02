package com.colofabrix.scala.figlet4s.figfont

import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.figlet4s.figfont.StandardFont._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class FIGfontSpecs extends AnyFlatSpec with Matchers with ValidatedMatchers with ValidatedValues {

  "FIGfont creation" should "succeed when data is valid" in {}

  // Required chars

  "FIGfont required chars" should "follow the FIGfont standard" in {}

  // Input Iterator

  "The Input Iterator" should "fail if the data ends before the full file is read" in {}

  it should "fail if a line of the Iterator is missing" in {}

  it should "fail there is an exception" in {}

  // Header

  "Header validation" should "fail when the header is invalid" in {}

  // Comments

  "Comments validation" should "fail if the lines of comments are incorrect" in {}

  // FIGcharacters

  "FIGcharacters validation" should "fail if required characters are missing" in {}

  it should "fail if the total number of characters doesn't respect the header" in {}

  it should "fail if a FIGcharacter is not valid" in {}

  it should "fail if the name of a tag is missing" in {}

  it should "fail if the name of a tag is an invalid value" in {}

  it should "fail if the comment of a tag is missing" in {}

}
