package com.colofabrix.scala.figlet4s.testutils.originalfiglet

import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.unsafe._
import java.io._
import scala.collection.concurrent.TrieMap
import scala.util.Using

/** Font access is slow on network filesystems: fonts are parsed once and copied once to the local temporary
  * directory, so both Figlet4s and the original figlet always read from a fast local disk
  */
object OriginalFigletFonts {

  private val fontCache =
    TrieMap.empty[String, FIGfont]

  private val fontMirror =
    TrieMap.empty[File, File]

  /** Returns the FIGfont with the given name, parsing it once and caching it */
  def cached(fontName: String): FIGfont =
    fontCache.getOrElseUpdate(fontName, Figlet4s.loadFontInternal(fontName))

  /** Returns a fast local copy of the file of the given FIGfont */
  def mirroredFile(font: FIGfont): File =
    fontMirror.getOrElseUpdate(
      font.file, {
        val mirror = File.createTempFile("figlet4s-font-", ".flf")
        Using.Manager { use =>
          use(new FileInputStream(font.file)).transferTo(use(new FileOutputStream(mirror)))
        }
        mirror
      },
    )

}
