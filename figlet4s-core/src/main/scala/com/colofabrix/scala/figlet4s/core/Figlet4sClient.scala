package com.colofabrix.scala.figlet4s.core

import java.io._
import scala.io._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.rendering._

/**
 * Layer of API internal to figlet4s, used to have uniform and generic access to resources when implementing client APIs
 */
private[figlet4s] object Figlet4sClient {

  /**
   * The name of the default internal font. This font is always guaranteed to be present.
   */
  val defaultFont: String = "standard"

  /**
   * The default value of the Max Width option
   */
  val defaultMaxWidth: Int = 80

  /**
   * The list of available internal fonts
   *
   * @tparam F A higher-kinded type for which there is a [[FigletSync]] instance
   * @return The collection of names of FIGfonts shipped with this library
   */
  def internalFonts[F[_]](implicit F: FigletSync[F]): F[Seq[String]] =
    F.map(FontListing.listInternalFonts)(x => x: Seq[String])

  /**
   * Loads one of the internal FIGfont
   *
   * @tparam F A higher-kinded type for which there is a [[FigletSync]] instance
   * @param name The name of the internal font to load, defaults to "standard"
   * @return The FIGfont of the requested internal font
   */
  def loadFontInternal[F[_]](name: String)(implicit F: FigletSync[F]): F[FigletResult[FIGfont]] =
    F.flatMap(fileDecoder(Codec.ISO8859)) { decoder =>
      FontFileReader.readInternal(s"fonts/$name.flf", decoder)((file, source) => createFIGfont(file, source))
    }

  /**
   * Loads a FIGfont from file
   *
   * @tparam F A higher-kinded type for which there is a [[FigletSync]] instance
   * @param path  The path of the font file to load. It can be a .flf file or a zipped file.
   * @param codec The encoding of the file if textual
   * @return The FIGfont loaded from the specified path
   */
  def loadFont[F[_]](path: String, codec: Codec)(implicit F: FigletSync[F]): F[FigletResult[FIGfont]] =
    F.flatMap(fileDecoder(codec)) { decoder =>
      FontFileReader.read(path, decoder)((file, source) => createFIGfont(file, source))
    }

  /**
   * Renders a given text as a FIGure
   *
   * @tparam F A higher-kinded type for which there is a [[FigletSync]] instance
   * @param text    The text to render
   * @param options The rendering options used to render the text
   * @return A FIGure representing the rendered text
   */
  def renderString[F[_]](text: String, options: RenderOptions)(implicit F: FigletSync[F]): F[FIGure] =
    F.delay {
      Rendering.render(text, options)
    }

  //  Support  //

  private def fileDecoder[F[_]](codec: Codec)(implicit F: FigletSync[F]): F[Codec] =
    F.pure {
      codec
        .decoder
        .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
    }

  private def createFIGfont[F[_]](file: File, source: BufferedSource)(implicit F: FigletSync[F]): F[FigletResult[FIGfont]] =
    F.delay {
      FIGfont(file, source.getLines())
    }

}
