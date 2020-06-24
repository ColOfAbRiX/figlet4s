package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.renderers._
import scala.io._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Figlet4s {
  import java.io.File

  /**
   * The list of available internal fonts
   */
  lazy val internalFonts: Vector[String] = {
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

  /**
   * Loads a string as a FIGure given a font name
   */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def renderString(text: String, fontName: String = "standard"): FIGure =
    loadFontInternal(fontName)
      .map(renderString(text, _))
      .fold(e => throw e.head, identity)

  /**
   * Loads a string as a FIGure given a FIGfont
   */
  def renderString(text: String, font: FIGfont): FIGure =
    HorizontalFittingRenderer.render(text, font)

  /**
   * Loads one of the internal FIGfont
   */
  def loadFontInternal(fontName: String = "standard"): FigletResult[FIGfont] = {
    val decoder = Codec("ISO8859_1")
      .decoder
      .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)

    val lines = Source
      .fromResource(s"fonts/$fontName.flf")(decoder)
      .getLines()
      .toVector

    FIGfont(fontName, lines)
  }

  /**
   * Load a FIGfont from file
   */
  def loadFont(fontPath: String, fontEncoding: String = "ISO8859_1"): FigletResult[FIGfont] = {
    val decoder = Codec(fontEncoding)
      .decoder
      .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)

    val fontFile = new File(fontPath)
    val fontName = fontFile.getName.split('.').init.mkString("")

    val lines = Source
      .fromFile(fontFile)(decoder)
      .getLines()
      .toVector

    FIGfont(fontName, lines)
  }
}
