package com.colofabrix.scala.figlet4s

import cats._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.rendering._
import java.io.File
import java.net._
import scala.io._

/**
 * Layer of API internal to figlet4s, used to have uniform and generic access to resources when implementing client APIs
 */
private[figlet4s] object Figlet4sClient {

  /**
   * The list of available internal fonts
   */
  def internalFonts[F[_]: Sync]: F[Vector[String]] = {
    val resources =
      getClass
        .getProtectionDomain
        .getCodeSource
        .getLocation

    val scheme =
      resources
        .toURI
        .getScheme

    scheme match {
      case "file" => internalFontsFromFile(resources.toURI)
      case "jar"  => internalFontsFromJar(resources)
      case _      => Sync[F].raiseError(FigletException(new RuntimeException("Could not determine the type of artifacts")))
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
      font    <- withResource(Source.fromResource(path)(decoder))(interpretFile[F](path))
    } yield font

  //  Support  //

  private def internalFontsFromFile[F[_]: Sync](resources: URI): F[Vector[String]] =
    Sync[F].delay {
      new java.io.File(resources.resolve("fonts"))
        .listFiles()
        .toVector
        .filter(path => path.getName.endsWith(".flf"))
        .map(_.getName.replace(".flf", ""))
    }

  //@SuppressWarnings(Array("org.wartremover.warts.Equals"))
  private def internalFontsFromJar[F[_]: Sync](resources: URL): F[Vector[String]] =
    withResource(new java.util.zip.ZipInputStream(resources.openStream)) { zip =>
      Sync[F].delay {
        Iterator
          .continually(zip.getNextEntry)
          .takeWhile(Option(_).isDefined)
          .map(zipEntry => new File(zipEntry.getName))
          .filter(path => path.getPath.startsWith("fonts") && path.getName.endsWith(".flf"))
          .map(_.getName.replace(".flf", ""))
          .toVector
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
    // Here I implement a version of opening a resource with a built-in suspension of opening a resource with exception
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
