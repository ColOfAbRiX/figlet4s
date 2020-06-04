package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.figfont._
import scala.io.Codec
import scala.io.Source

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Main extends App {
  val fonts = List(
    "1row.flf",
    "3-d.flf",
    "3x5.flf",
    "4max.flf",
    "5lineoblique.flf",
    "B1FF.flf",
    "acrobatic.flf",
    "alligator.flf",
    "alligator2.flf",
    // "alligator3.flf",
    "alpha.flf",
    "alphabet.flf",
    "amc3line.flf",
    "amc3liv1.flf",
    "amcaaa01.flf",
    "amcneko.flf",
    "amcrazo2.flf",
    "amcrazor.flf",
    "amcslash.flf",
    "amcslder.flf",
    "amcthin.flf",
    "amctubes.flf",
    "amcun1.flf",
    "arrows.flf",
    "avatar.flf",
    "banner.flf",
    "banner3-D.flf",
    "banner3.flf",
    "banner4.flf",
    "barbwire.flf",
    "basic.flf",
    "bell.flf",
    "benjamin.flf",
    "big.flf",
    "bigchief.flf",
    "bigfig.flf",
    "binary.flf",
    "block.flf",
    "bolger.flf",
    "bright.flf",
    "broadway.flf",
    "bubble.flf",
    "bulbhead.flf",
    "calgphy2.flf",
    "caligraphy.flf",
    "catwalk.flf",
    "chunky.flf",
    "coinstak.flf",
    "colossal.flf",
    "computer.flf",
    "contessa.flf",
    "contrast.flf",
    "cosmic.flf",
    "cosmike.flf",
    "crawford.flf",
    "cricket.flf",
    "cyberlarge.flf",
    "cybermedium.flf",
    "cybersmall.flf",
    "decimal.flf",
    "diamond.flf",
    "digital.flf",
    "doh.flf",
    "doom.flf",
    "dosrebel.flf",
    "dotmatrix.flf",
    // "double.flf",
    "drpepper.flf",
    "eftichess.flf",
    "eftifont.flf",
    "eftipiti.flf",
    "eftirobot.flf",
    "eftitalic.flf",
    "eftiwall.flf",
    "eftiwater.flf",
    "epic.flf",
    "fender.flf",
    "fourtops.flf",
    "fraktur.flf",
    "fuzzy.flf",
    "goofy.flf",
    "gothic.flf",
    "gradient.flf",
    "graffiti.flf",
    "greek.flf",
    "henry3d.flf",
    "hex.flf",
    "hollywood.flf",
    "invita.flf",
    "isometric1.flf",
    "isometric2.flf",
    "isometric3.flf",
    "isometric4.flf",
    "italic.flf",
    "ivrit.flf",
    "jacky.flf",
    "jazmine.flf",
    "jerusalem.flf",
    "katakana.flf",
    "kban.flf",
    "keyboard.flf",
    // "konto.flf",
    // "kontoslant.flf",
    "larry3d.flf",
    "lcd.flf",
    "lean.flf",
    "letters.flf",
    "linux.flf",
    "lockergnome.flf",
    "madrid.flf",
    "marquee.flf",
    "maxfour.flf",
    "mike.flf",
    "mini.flf",
    "mirror.flf",
    "mnemonic.flf",
    "morse.flf",
    "moscow.flf",
    "nancyj-fancy.flf",
    "nancyj-improved.flf",
    "nancyj-underlined.flf",
    "nancyj.flf",
    "nipples.flf",
    "nscript.flf",
    "ntgreek.flf",
    "o8.flf",
    "octal.flf",
    "ogre.flf",
    "oldbanner.flf",
    "os2.flf",
    "pawp.flf",
    "peaks.flf",
    "peaksslant.flf",
    "pebbles.flf",
    "pepper.flf",
    "poison.flf",
    "puffy.flf",
    // "pyramid.flf",
    "rectangles.flf",
    "redphoenix.flf",
    "relief.flf",
    "relief2.flf",
    "reverse.flf",
    "roman.flf",
    "rot13.flf",
    "rounded.flf",
    "rowancap.flf",
    "rozzo.flf",
    "runic.flf",
    "s-relief.flf",
    "santaclara.flf",
    "sblood.flf",
    "script.flf",
    "serifcap.flf",
    "shadow.flf",
    "shimrod.flf",
    "short.flf",
    "slant.flf",
    "slide.flf",
    "slscript.flf",
    "small.flf",
    "smisome1.flf",
    "smkeyboard.flf",
    "smpoison.flf",
    "smscript.flf",
    "smshadow.flf",
    "smslant.flf",
    "smtengwar.flf",
    "speed.flf",
    "stacey.flf",
    "stampate.flf",
    "stampatello.flf",
    "standard.flf",
    "starwars.flf",
    "stellar.flf",
    "stop.flf",
    "straight.flf",
    "sub-zero.flf",
    "tanja.flf",
    "tengwar.flf",
    "term.flf",
    "thick.flf",
    "thin.flf",
    "threepoint.flf",
    "ticks.flf",
    "ticksslant.flf",
    "tiles.flf",
    "tinker-toy.flf",
    "tombstone.flf",
    "trek.flf",
    "tsalagi.flf",
    "tubular.flf",
    "twopoint.flf",
    "univers.flf",
    "usaflag.flf",
    "weird.flf",
    "whimsy.flf",
  )

  // // https://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html
  // val decoder = Codec("ISO8859_1").decoder.onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)

  // val fontName = "standard"
  // val lines    = Source.fromResource(s"fonts/$fontName.flf")(decoder).getLines().toVector
  // val fontV    = FIGfont(fontName, lines)
  // fontV match {
  //   case Invalid(e) =>
  //     pprint.pprintln(e)
  //   case Valid(font) =>
  //     pprint.pprintln(font)
  //     printFont("Fabrizio & Claire")(font)
  // }

  // for (font <- fonts) {
  //   println(s"Font: $font")

  //   val lines = Source.fromResource(s"fonts/$font")(decoder).getLines().toVector
  //   val fontV = FIGfont(font, lines)

  //   fontV match {
  //     case Invalid(e)  => pprint.pprintln(e)
  //     case Valid(font) => //printFont("Fabrizio & Claire")(font)
  //   }

  //   println("")
  // }

  def printFont(name: String)(font: FIGfont): Unit =
    for (l <- 0 until font.header.height) {
      for (c <- name) print(font.process(c)(l))
      print("\n")
    }
}

object Figlet4s {
  import java.io.File

  def listFonts(): Vector[String] = {
    import java.util.zip.ZipInputStream

    // Opening the JAR to look at resources
    val jar = getClass.getProtectionDomain.getCodeSource.getLocation
    val zip = new ZipInputStream(jar.openStream)

    Iterator
      .continually(zip.getNextEntry)
      .takeWhile(_ != null)
      .map(zipEntry => new File(zipEntry.getName))
      .filter(path => path.getPath.startsWith("fonts") && path.getName.endsWith(".flf"))
      .map(_.getName.replace(".flf", ""))
      .toVector
  }

  def loadString(
      text: String,
      fontName: String = "standard",
      fontEncoding: String = "ISO8859_1",
  ): FigletResult[FIGstring] = {
    loadFont(fontName, fontEncoding).map(FIGstring(text, _))
  }

  def loadString(text: String, font: FIGfont): FIGstring =
    FIGstring(text, font)

  def loadFont(fontName: String = "standard", fontEncoding: String = "ISO8859_1"): FigletResult[FIGfont] = {
    val decoder = Codec(fontEncoding)
      .decoder
      .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)

    val lines = Source
      .fromResource(s"fonts/$fontName.flf")(decoder)
      .getLines()
      .toVector

    FIGfont(fontName, lines)
  }

}
