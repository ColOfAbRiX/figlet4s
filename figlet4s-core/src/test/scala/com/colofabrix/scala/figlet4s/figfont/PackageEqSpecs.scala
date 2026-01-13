package com.colofabrix.scala.figlet4s.figfont

import cats.scalatest._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class PackageEqSpecs extends AnyFlatSpec with Matchers with ValidatedMatchers with ValidatedValues {

  //  FIGfont  //

  trait FIGfontScope {

    val fontA1: FIGfont = Figlet4s.loadFontInternal("standard")
    val fontA2: FIGfont = Figlet4s.loadFontInternal("standard")
    val fontB: FIGfont  = Figlet4s.loadFontInternal("alligator")

  }

  "FIGfont Equality" should "return true when given the same instance" in new FIGfontScope {
    (fontA1 === fontA1) should equal(true)
  }

  it should "return true when given two identical instances" in new FIGfontScope {
    (fontA1 === fontA2) should equal(true)
    (fontA2 === fontA1) should equal(true)
  }

  it should "return false when given different instances" in new FIGfontScope {
    (fontA1 === fontB) should equal(false)
    (fontB === fontA1) should equal(false)
  }

  //  FIGcharacter  //

  trait FIGcharacterScope {

    private val font         = Figlet4s.loadFontInternal("standard")
    val charA1: FIGcharacter = font('A')
    val charA2: FIGcharacter = font('A')
    val charB: FIGcharacter  = font('B')

  }

  "FIGcharacter Equality" should "return true when given the same instance" in new FIGcharacterScope {
    (charA1 === charA1) should equal(true)
  }

  it should "return true when given two identical instances" in new FIGcharacterScope {
    (charA1 === charA2) should equal(true)
    (charA2 === charA1) should equal(true)
  }

  it should "return false when given different instances" in new FIGcharacterScope {
    (charA1 === charB) should equal(false)
    (charB === charA1) should equal(false)
  }

  //  FIGheader  //

  trait FIGheaderScope {

    val headerA1: FIGheader = Figlet4s.loadFontInternal("standard").header
    val headerA2: FIGheader = Figlet4s.loadFontInternal("standard").header
    val headerB: FIGheader  = Figlet4s.loadFontInternal("alligator").header

  }

  "FIGheader Equality" should "return true when given the same instance" in new FIGheaderScope {
    (headerA1 === headerA1) should equal(true)
  }

  it should "return true when given two identical instances" in new FIGheaderScope {
    (headerA1 === headerA2) should equal(true)
    (headerA2 === headerA1) should equal(true)
  }

  it should "return false when given different instances" in new FIGheaderScope {
    (headerA1 === headerB) should equal(false)
    (headerB === headerA1) should equal(false)
  }

  //  FIGfontSettings  //

  trait FIGfontSettingsScope {

    val settingsA1: FIGfontSettings = Figlet4s.loadFontInternal("standard").settings
    val settingsA2: FIGfontSettings = Figlet4s.loadFontInternal("standard").settings
    val settingsB: FIGfontSettings  = Figlet4s.loadFontInternal("alligator").settings

  }

  "FIGfontSettings Equality" should "return true when given the same instance" in new FIGfontSettingsScope {
    (settingsA1 === settingsA1) should equal(true)
  }

  it should "return true when given two identical instances" in new FIGfontSettingsScope {
    (settingsA1 === settingsA2) should equal(true)
    (settingsA2 === settingsA1) should equal(true)
  }

  it should "return false when given different instances" in new FIGfontSettingsScope {
    (settingsA1 === settingsB) should equal(false)
    (settingsB === settingsA1) should equal(false)
  }

  //  SubColumns  //

  trait SubColumnsScope {

    val itemA1: SubColumns = SubColumns(Seq("A", "B", "C"))
    val itemA2: SubColumns = SubColumns(Seq("A", "B", "C"))
    val itemB: SubColumns  = SubColumns(Seq("D", "E", "F"))

  }

  "SubColumns Equality" should "return true when given the same instance" in new SubColumnsScope {
    (itemA1 === itemA1) should equal(true)
  }

  it should "return true when given two identical instances" in new SubColumnsScope {
    (itemA1 === itemA2) should equal(true)
    (itemA2 === itemA1) should equal(true)
  }

  it should "return false when given different instances" in new SubColumnsScope {
    (itemA1 === itemB) should equal(false)
    (itemB === itemA1) should equal(false)
  }

}
