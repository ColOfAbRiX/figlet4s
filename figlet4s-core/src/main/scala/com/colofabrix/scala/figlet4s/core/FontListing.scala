package com.colofabrix.scala.figlet4s.core

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import java.io._
import java.net._
import java.nio.file._
import java.util.zip._

private[figlet4s] object FontListing {

  private lazy val fontsDirectoryName: String = "fonts/"

  /**
   * The list of available internal fonts
   *
   * @tparam F A higher-kinded type for which there is a [[cats.effect.Sync]] instance
   * @return The collection of names of FIGfonts shipped with this library
   */
  def listInternalFonts[F[_]: Sync]: F[Vector[String]] = {
    val resources =
      getClass
        .getProtectionDomain
        .getCodeSource
        .getLocation
        .toURI

    val file = new File(resources)

    if (file.isDirectory)
      fromDirectory(Paths.get(resources.resolve(fontsDirectoryName)).toString)
    else if (file.getName.toLowerCase.endsWith(".jar"))
      fromJar(resources)
    else
      Sync[F].raiseError {
        FigletError("Could not determine the type of artifacts where I can find the fonts")
      }
  }

  //  Support  //

  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  private def fromDirectory[F[_]: Sync](startPath: String): F[Vector[String]] = {
    def recurse(currentPath: String): F[Vector[String]] =
      Braket.withResource(openPath(currentPath)) { reader =>
        Iterator
          .continually(reader.readLine)
          .takeWhile(Option(_).isDefined)
          .map(Paths.get(currentPath, _).toString)
          .toVector
          .flatTraverse[F, String] {
            case path if new File(path).isDirectory =>
              recurse(path)
            case path if path.toLowerCase.endsWith(".flf") =>
              Sync[F].pure(Vector(path.substring(startPath.length + 1, path.length - 4)))
            case _ =>
              Sync[F].pure(Vector.empty)
          }
      }
      recurse(startPath)
  }

  private def openPath(path: String): BufferedReader = {
    val stream = Paths.get(path).toUri.toURL.openStream
    new BufferedReader(new InputStreamReader(stream))
  }

  private def fromJar[F[_]: Sync](resources: URI): F[Vector[String]] =
    Braket.withResource(new ZipInputStream(resources.toURL.openStream)) { zip =>
      Sync[F].delay(listJar(zip))
    }

  private def listJar[F[_]: Sync](reader: ZipInputStream): Vector[String] =
    Iterator
      .continually(reader.getNextEntry)
      .takeWhile(Option(_).isDefined)
      .map(_.getName)
      .collect {
        case path if path.startsWith(fontsDirectoryName) && path.toLowerCase.endsWith(".flf") =>
          path.substring(fontsDirectoryName.length + 1, path.length - 4)
      }
      .toVector

}
