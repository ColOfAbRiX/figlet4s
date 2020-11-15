package com.colofabrix.scala.figlet4s

import cats._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.rendering._
import java.io._
import java.net._
import scala.io._

/**
 * Layer of API internal to figlet4s, used to have uniform and generic access to resources when implementing client APIs
 */
private[figlet4s] object Figlet4sClient {

  /**
   * The list of available internal fonts
   *
   * @tparam F A higher-kinded type for which there is a [[cats.effect.Sync]] instance
   * @return The collection of names of FIGfonts shipped with this library
   */
  def internalFonts[F[_]: Sync]: F[Seq[String]] = {
    val resources =
      getClass
        .getProtectionDomain
        .getCodeSource
        .getLocation
        .toURI

    val file = new File(resources)

    if (file.isDirectory)
      internalFontsFromDirectory(resources).map(_.toSeq)
    else if (file.getName.toLowerCase.endsWith(".jar"))
      internalFontsFromJar(resources).map(_.toSeq)
    else
      Sync[F].raiseError {
        FigletError("Could not determine the type of artifacts to open to find the fonts")
      }
  }

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
      HorizontalTextRenderer.render(text, options)
    }

  /**
   * Loads one of the internal FIGfont
   *
   * @tparam F A higher-kinded type for which there is a [[cats.effect.Sync]] instance
   * @param name The name of the internal font to load, defaults to "standard"
   * @return The FIGfont of the requested internal font
   */
  def loadFontInternal[F[_]: Sync](name: String): F[FigletResult[FIGfont]] =
    for {
      path    <- Sync[F].pure(s"fonts/$name.flf")
      decoder <- fileDecoder[F](Codec.ISO8859)
      font    <- withResource(sourceFromResource(path, decoder))(interpretFigletFile[F](path))
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
      font    <- withResource(Source.fromFile(path)(decoder))(interpretFigletFile[F](path))
    } yield font

  /**
   * The name of the default internal font. This font is always guaranteed to be present.
   */
  val defaultFont: String = "standard"

  /**
   * The default value of the Max Width option
   */
  val defaultMaxWidth: Int = Int.MaxValue

  //  Support  //

  private def internalFontsFromDirectory[F[_]: Sync](resources: URI): F[List[String]] =
    withResource(new BufferedReader(new InputStreamReader(resources.resolve("fonts").toURL.openStream()))) { reader =>
      Sync[F].delay {
        Iterator
          .continually(reader.readLine)
          .takeWhile(Option(_).isDefined)
          .withFilter(path => path.endsWith(".flf"))
          .map(_.replace(".flf", ""))
          .toList
      }
    }

  private def internalFontsFromJar[F[_]: Sync](resources: URI): F[List[String]] =
    withResource(new java.util.zip.ZipInputStream(resources.toURL.openStream)) { zip =>
      Sync[F].delay {
        Iterator
          .continually(zip.getNextEntry)
          .takeWhile(Option(_).isDefined)
          .map(zipEntry => new File(zipEntry.getName))
          .withFilter(path => path.getPath.startsWith("fonts") && path.getName.endsWith(".flf"))
          .map(_.getName.replace(".flf", ""))
          .toList
      }
    }

  private def fileDecoder[F[_]: Applicative](codec: Codec): F[Codec] =
    Applicative[F].pure {
      codec
        .decoder
        .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
    }

  private def interpretFigletFile[F[_]: Sync](path: String)(source: BufferedSource): F[FigletResult[FIGfont]] =
    Sync[F].pure {
      val name  = new File(path).getName.split('.').init.mkString("")
      val lines = source.getLines()
      FIGfont(name, lines)
    }

  private def sourceFromResource(fileName: String, codec: Codec): BufferedSource =
    Source.fromInputStream(this.getClass.getClassLoader.getResourceAsStream(fileName))(codec)

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  private def withResource[F[_]: MonadThrowable, R <: AutoCloseable, A](resource: => R)(f: R => F[A]): F[A] = {
    // I want to support Id as an effect for this library so that non-FP users can avoid dealing with monads. It's not
    // possible to create a real Sync[Id] instance for Id because Id doesn't support a true suspension of effects.
    // Here I implement a version of opening a resource with a built-in suspension for the acquisition with exception
    // reporting in MonadError. See also: https://medium.com/@dkomanov/scala-try-with-resources-735baad0fd7d
    var exception: Throwable = null
    try {
      f(resource)
    } catch {
      case e: Throwable =>
        exception = e
        MonadThrowable[F].raiseError[A](FigletLoadingError(e.getMessage, e))
    } finally {
      if (exception == null) resource.close()
      else
        try {
          resource.close()
        } catch {
          case suppressed: Throwable =>
            exception.addSuppressed(suppressed)
        }
    }
  }

}
