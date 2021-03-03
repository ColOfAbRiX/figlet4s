package com.colofabrix.scala.figlet4s.core

import java.io._
import java.nio.file.Paths
import scala.io._
import cats._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
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
   * @tparam F A higher-kinded type for which there is a [[cats.effect.Sync]] instance
   * @return The collection of names of FIGfonts shipped with this library
   */
  def internalFonts[F[_]: Sync]: F[Seq[String]] =
    FontListing.listInternalFonts

  /**
   * Loads one of the internal FIGfont
   *
   * @tparam F A higher-kinded type for which there is a [[cats.effect.Sync]] instance
   * @param name The name of the internal font to load, defaults to "standard"
   * @return The FIGfont of the requested internal font
   */
  def loadFontInternal[F[_]: Sync](name: String): F[FigletResult[FIGfont]] =
    for {
      path    <- Sync[F].pure(Paths.get("fonts", s"$name.flf").toString)
      decoder <- fileDecoder[F](Codec.ISO8859)
      font    <- FontFileReader.readInternal(path, decoder)(interpretFigletFile[F](path))
    } yield font

  /**
   * Loads a FIGfont from file
   *
   * @tparam F A higher-kinded type for which there is a [[cats.effect.Sync]] instance
   * @param path  The path of the font file to load. It can be a .flf file or a zipped file.
   * @param codec The encoding of the file if textual
   * @return The FIGfont loaded from the specified path
   */
  def loadFont[F[_]: Sync](path: String, codec: Codec): F[FigletResult[FIGfont]] =
    for {
      decoder <- fileDecoder[F](codec)
      font    <- FontFileReader.read(path, decoder)(interpretFigletFile[F](path))
    } yield font

  /**
   * Renders a given text as a FIGure
   *
   * @tparam F A higher-kinded type for which there is a [[cats.effect.Sync]] instance
   * @param text    The text to render
   * @param options The rendering options used to render the text
   * @return A FIGure representing the rendered text
   */
  def renderString[F[_]: Sync](text: String, options: RenderOptions): F[FIGure] =
    Sync[F].pure {
      Rendering.render(text, options)
    }

  //  Support  //

  private def fileDecoder[F[_]: Applicative](codec: Codec): F[Codec] =
    Applicative[F].pure {
      codec
        .decoder
        .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
    }

  private def interpretFigletFile[F[_]: Sync](path: String)(source: BufferedSource): F[FigletResult[FIGfont]] =
    Sync[F].delay {
      val name = new File(path).getName.split('.').init.mkString("")
      FIGfont(name, source.getLines())
    }

}
