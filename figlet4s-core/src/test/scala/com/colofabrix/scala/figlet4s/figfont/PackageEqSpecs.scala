package com.colofabrix.scala.figlet4s.figfont

import cats.scalatest._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class PackageEqSpecs extends AnyFlatSpec with Matchers with ValidatedMatchers with ValidatedValues {

  //  FIGfont  //

  private val fontA1: FIGfont = Figlet4s.loadFontInternal("standard")
  private val fontA2: FIGfont = Figlet4s.loadFontInternal("standard")
  private val fontB: FIGfont  = Figlet4s.loadFontInternal("alligator")

  "FIGfont Equality" should "return true when given the same instance" in {
    (fontA1 === fontA1) should equal(true)
  }

  it should "return true when given two identical instances" in {
    (fontA1 === fontA2) should equal(true)
    (fontA2 === fontA1) should equal(true)
  }

  it should "return false when given different instances" in {
    (fontA1 === fontB) should equal(false)
    (fontB === fontA1) should equal(false)
  }

  //  FIGcharacter  //

  private val font   = Figlet4s.loadFontInternal("standard")
  private val charA1 = font('A')
  private val charA2 = font('A')
  private val charB  = font('B')

  "FIGcharacter Equality" should "return true when given the same instance" in {
    (charA1 === charA1) should equal(true)
  }

  it should "return true when given two identical instances" in {
    (charA1 === charA2) should equal(true)
    (charA2 === charA1) should equal(true)
  }

  it should "return false when given different instances" in {
    (charA1 === charB) should equal(false)
    (charB === charA1) should equal(false)
  }

  //  FIGheader  //

  private val headerA1: FIGheader = Figlet4s.loadFontInternal("standard").header
  private val headerA2: FIGheader = Figlet4s.loadFontInternal("standard").header
  private val headerB: FIGheader  = Figlet4s.loadFontInternal("alligator").header

  "FIGheader Equality" should "return true when given the same instance" in {
    (headerA1 === headerA1) should equal(true)
  }

  it should "return true when given two identical instances" in {
    (headerA1 === headerA2) should equal(true)
    (headerA2 === headerA1) should equal(true)
  }

  it should "return false when given different instances" in {
    (headerA1 === headerB) should equal(false)
    (headerB === headerA1) should equal(false)
  }

  //  FIGfontSettings  //

  private val settingsA1: FIGfontSettings = Figlet4s.loadFontInternal("standard").settings
  private val settingsA2: FIGfontSettings = Figlet4s.loadFontInternal("standard").settings
  private val settingsB: FIGfontSettings  = Figlet4s.loadFontInternal("alligator").settings

  "FIGfontSettings Equality" should "return true when given the same instance" in {
    (settingsA1 === settingsA1) should equal(true)
  }

  it should "return true when given two identical instances" in {
    (settingsA1 === settingsA2) should equal(true)
    (settingsA2 === settingsA1) should equal(true)
  }

  it should "return false when given different instances" in {
    (settingsA1 === settingsB) should equal(false)
    (settingsB === settingsA1) should equal(false)
  }

  //  SubColumns  //

  private val itemA1: SubColumns = SubColumns(Seq("A", "B", "C"))
  private val itemA2: SubColumns = SubColumns(Seq("A", "B", "C"))
  private val itemB: SubColumns  = SubColumns(Seq("D", "E", "F"))

  "SubColumns Equality" should "return true when given the same instance" in {
    (itemA1 === itemA1) should equal(true)
  }

  it should "return true when given two identical instances" in {
    (itemA1 === itemA2) should equal(true)
    (itemA2 === itemA1) should equal(true)
  }

  it should "return false when given different instances" in {
    (itemA1 === itemB) should equal(false)
    (itemB === itemA1) should equal(false)
  }
}
