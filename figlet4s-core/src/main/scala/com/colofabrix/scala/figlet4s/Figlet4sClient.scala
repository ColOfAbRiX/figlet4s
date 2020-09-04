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
   */
  def internalFonts[F[_]: Sync]: F[List[String]] = {
    val resources =
      getClass
        .getProtectionDomain
        .getCodeSource
        .getLocation
        .toURI

    val file = new File(resources)

    if (file.isDirectory)
      internalFontsFromDirectory(resources)
    else if (file.getName.toLowerCase.endsWith(".jar"))
      internalFontsFromJar(resources)
    else
      Sync[F].raiseError {
        FigletException(new RuntimeException("Could not determine the type of artifacts"))
      }
  }

  /**
   * Renders a given text as a FIGure
   */
  def renderString[F[_]: Sync](text: String, options: RenderOptions): F[FIGure] =
    Sync[F].pure {
      HorizontalTextRenderer.render(text, options)
    }

  /**
   * Loads one of the internal FIGfont
   */
  def loadFontInternal[F[_]: Sync](name: String = "standard"): F[FigletResult[FIGfont]] =
    for {
      path    <- Sync[F].pure(s"fonts/$name.flf")
      decoder <- fileDecoder[F]("ISO8859_1")
      font    <- withResource(Source.fromResource(path)(decoder))(interpretFile[F](path))
    } yield font

  /**
   * Loads a FIGfont from file
   */
  def loadFont[F[_]: Sync](path: String, encoding: String): F[FigletResult[FIGfont]] =
    for {
      decoder <- fileDecoder[F](encoding)
      font    <- withResource(Source.fromFile(path)(decoder))(interpretFile[F](path))
    } yield font

  //  Support  //

  private def internalFontsFromDirectory[F[_]: Sync](resources: URI): F[List[String]] =
    withResource(new BufferedReader(new InputStreamReader(resources.resolve("fonts").toURL.openStream()))) { reader =>
      Sync[F].delay {
        Iterator
          .continually(reader.readLine)
          .takeWhile(Option(_).isDefined)
          .filter(path => path.endsWith(".flf"))
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
          .filter(path => path.getPath.startsWith("fonts") && path.getName.endsWith(".flf"))
          .map(_.getName.replace(".flf", ""))
          .toList
      }
    }

  private def fileDecoder[F[_]: Applicative](encoding: String): F[Codec] =
    Applicative[F].pure {
      Codec(encoding)
        .decoder
        .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
    }

  private def interpretFile[F[_]: Applicative](path: String)(source: BufferedSource): F[FigletResult[FIGfont]] =
    Applicative[F].pure {
      val name  = new File(path).getName.split('.').init.mkString("")
      val lines = source.getLines()
      FIGfont(name, lines)
    }

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
