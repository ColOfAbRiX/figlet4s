package com.colofabrix.scala.figlet4s

import cats._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.renderers._
import java.io.File
import scala.io._

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
private[this] object Figlet4sAPI {
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
  def renderString[F[_]: Monad](text: String, options: RenderOptions): F[FIGure] =
    Monad[F].pure {
      HorizontalTextRenderer.render(text, options)
    }

  /**
   * Loads one of the internal FIGfont
   */
  def loadFontInternal[F[_]: Sync](name: String = "standard"): F[FigletResult[FIGfont]] =
    for {
      path    <- Sync[F].pure(s"fonts/$name.flf")
      decoder <- fileDecoder()
      result  <- Resource.fromAutoCloseable(acquireResource(path, decoder)).use(interpretFile(path))
    } yield result

  /**
   * Load a FIGfont from file
   */
  def loadFont[F[_]: Sync](path: String, encoding: String = "ISO8859_1"): F[FigletResult[FIGfont]] =
    for {
      decoder <- fileDecoder(encoding)
      result  <- Resource.fromAutoCloseable(acquireFile(path, decoder)).use(interpretFile(path))
    } yield result

  //  Support  //

  private def fileDecoder[F[_]: Sync](encoding: String = "ISO8859_1"): F[Codec] =
    Sync[F].pure {
      Codec(encoding)
        .decoder
        .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
    }

  private def acquireFile[F[_]: Sync](path: String, decoder: Codec): F[BufferedSource] =
    Sync[F].delay {
      Source.fromFile(new File(path))(decoder)
    }

  private def acquireResource[F[_]: Sync](path: String, decoder: Codec): F[BufferedSource] =
    Sync[F].delay {
      Source.fromResource(path)(decoder)
    }

  private def interpretFile[F[_]: Sync](path: String)(source: BufferedSource): F[FigletResult[FIGfont]] =
    Sync[F].delay {
      val name  = new File(path).getName.split('.').init.mkString("")
      val lines = source.getLines().toVector
      FIGfont(name, lines)
    }
}

object Figlet4sIO {
  import cats.effect._

  def internalFonts: IO[Vector[String]] =
    Figlet4sAPI.internalFonts[IO]

  def renderString(text: String, options: RenderOptions): IO[FIGure] =
    Figlet4sAPI.renderString[IO](text, options)

  def loadFontInternal(name: String = "standard"): IO[FIGfont] =
    Figlet4sAPI
      .loadFontInternal[IO](name)
      .flatMap(errorsAsIo)

  def loadFont(path: String, encoding: String = "ISO8859_1"): IO[FIGfont] =
    Figlet4sAPI
      .loadFont[IO](path, encoding)
      .flatMap(errorsAsIo)

  private def errorsAsIo[A](x: FigletResult[A]): IO[A] =
    x.fold(
      e => IO.raiseError(e.head),
      x => IO(x),
    )
}

object Figlet4sUnsafe {
  import cats.effect.Sync
  import cats.Id

  def internalFonts: Vector[String] =
    Figlet4sAPI.internalFonts[Id]

  def renderString(text: String, options: RenderOptions): FIGure =
    Figlet4sAPI.renderString[Id](text, options)

  def loadFontInternal(name: String = "standard"): FIGfont =
    Figlet4sAPI
      .loadFontInternal[Id](name)
      .fold(e => throw e.head, identity)

  def loadFont(path: String, encoding: String = "ISO8859_1"): FIGfont =
    Figlet4sAPI
      .loadFont[Id](path, encoding)
      .fold(e => throw e.head, identity)

  implicit private val syncId: Sync[Id] = new Sync[Id] {
    def suspend[A](thunk: => Id[A]): Id[A] =
      thunk

    def bracketCase[A, B](acquire: Id[A])(use: A => Id[B])(release: (A, ExitCase[Throwable]) => Id[Unit]): Id[B] =
      ???

    def pure[A](x: A): Id[A] =
      x

    def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
      f(fa)

    def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] =
      ???

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    def raiseError[A](e: Throwable): Id[A] =
      throw e

    def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
      ???
  }
}
