package com.colofabrix.scala.figlet4s

import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.renderers._
import java.io.File
import scala.io._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
object Figlet4s {
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
   * Renders a given text as a FIGure
   */
  def renderString(text: String, options: RenderOptions): FIGure = {
    HorizontalTextRenderer.render(text, options)
  }

  /**
   * Loads one of the internal FIGfont
   */
  def loadFontInternal(name: String = "standard"): FigletResult[FIGfont] = {
    val decoder = Codec("ISO8859_1")
      .decoder
      .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)

    val lines = Source
      .fromResource(s"fonts/$name.flf")(decoder)
      .getLines()
      .toVector

    FIGfont(name, lines)
  }

  /**
   * Load a FIGfont from file
   */
  def loadFont(path: String, encoding: String = "ISO8859_1"): FigletResult[FIGfont] = {
    val decoder = Codec(encoding)
      .decoder
      .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)

    val file = new File(path)
    val name = file.getName.split('.').init.mkString("")

    val lines = Source
      .fromFile(file)(decoder)
      .getLines()
      .toVector

    FIGfont(name, lines)
  }
}
