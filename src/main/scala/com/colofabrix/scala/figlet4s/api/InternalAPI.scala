package com.colofabrix.scala.figlet4s.api

import cats._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.rendering._
import java.io.File
import scala.io._
import com.colofabrix.scala.figlet4s.options.RenderOptions

/**
 * Layer of API internal to figlet4s, used to have uniform and generic access to resources when implementing client APIs
 */
private[figlet4s] object InternalAPI {

  /**
   * The list of available internal fonts
   */
  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def internalFonts[F[_]: Sync]: F[Vector[String]] =
    Sync[F].delay {
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
  def renderString[F[_]: Sync](text: String, options: RenderOptions): F[FIGure] =
    Monad[F].pure {
      HorizontalTextRenderer.render(text, options)
    }

  /**
   * Loads one of the internal FIGfont
   */
  def loadFontInternal[F[_]: Sync](name: String = "standard"): F[FigletResult[FIGfont]] =
    for {
      path    <- Sync[F].pure(s"fonts/$name.flf")
      decoder <- fileDecoder("ISO8859_1")
      font    <- withResource(path, decoder).use(interpretFile[F](path))
    } yield font

  /**
   * Loads a FIGfont from file
   */
  def loadFont[F[_]: Sync](path: String, encoding: String): F[FigletResult[FIGfont]] =
    for {
      decoder <- fileDecoder(encoding)
      font    <- withFile(path, decoder).use(interpretFile[F](path))
    } yield font

  //  Support  //

  private def fileDecoder[F[_]: Sync](encoding: String): F[Codec] =
    Sync[F].pure {
      Codec(encoding)
        .decoder
        .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
    }

  private def withFile[F[_]: Sync](path: String, decoder: Codec): Resource[F, BufferedSource] =
    Resource.liftF {
      Sync[F].delay(Source.fromFile(new File(path))(decoder))
    }

  private def withResource[F[_]: Sync](path: String, decoder: Codec): Resource[F, BufferedSource] =
    Resource.liftF {
      Sync[F].delay(Source.fromResource(path)(decoder))
    }

  private def interpretFile[F[_]: Sync](path: String)(source: BufferedSource): F[FigletResult[FIGfont]] =
    Sync[F].delay {
      val name  = new File(path).getName.split('.').init.mkString("")
      val lines = source.getLines().toVector
      FIGfont(name, lines)
    }
}
