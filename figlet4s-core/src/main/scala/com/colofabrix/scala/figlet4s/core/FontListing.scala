package com.colofabrix.scala.figlet4s.core

import com.colofabrix.scala.figlet4s.errors._
import java.io._
import java.net._
import java.nio.file._
import java.util.zip._
import java.util.Locale

private[figlet4s] object FontListing {

  private lazy val fontsDirectoryName: String = "fonts/"

  /**
   * The list of available internal fonts
   *
   * @tparam F A higher-kinded type for which there is a [[FigletSync]] instance
   * @return The collection of names of FIGfonts shipped with this library
   */
  def listInternalFonts[F[_]](implicit F: FigletSync[F]): F[Vector[String]] =
    F.flatMap(LibraryLocation.discover) {
      case LibraryLocation.FileSystem(location, _) =>
        fromDirectory(Paths.get(location.resolve(fontsDirectoryName)).toString)
      case LibraryLocation.Jar(location) =>
        fromJar(location)
      case LibraryLocation.Unknown =>
        F.raiseError {
          FigletError("Could not determine the type of artifacts where I can find the fonts")
        }
    }

  //  Support  //

  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  private def fromDirectory[F[_]](startPath: String)(implicit F: FigletSync[F]): F[Vector[String]] = {
    def recurse(currentPath: String): F[Vector[String]] =
      F.withResource(openPath(currentPath)) { reader =>
        val paths =
          Iterator
            .continually(reader.readLine)
            .takeWhile(Option(_).isDefined)
            .map(Paths.get(currentPath, _).toString)
            .toVector

        paths.foldLeft(F.pure(Vector.empty[String])) { (collected, path) =>
          F.flatMap(collected) { names =>
            path match {
              case p if new File(p).isDirectory =>
                F.map(recurse(p))(names ++ _)
              case p if p.toLowerCase(Locale.ROOT).endsWith(".flf") =>
                F.pure(names :+ p.substring(startPath.length + 1, p.length - 4))
              case _ =>
                F.pure(names)
            }
          }
        }
      }
      
    recurse(startPath)
  }

  private def openPath(path: String): BufferedReader = {
    val stream = Paths.get(path).toUri.toURL.openStream
    new BufferedReader(new InputStreamReader(stream))
  }

  private def fromJar[F[_]](resources: URI)(implicit F: FigletSync[F]): F[Vector[String]] =
    F.withResource(new ZipInputStream(resources.toURL.openStream)) { zip =>
      F.delay(listJar(zip))
    }

  private def listJar(reader: ZipInputStream): Vector[String] =
    Iterator
      .continually(reader.getNextEntry)
      .takeWhile(Option(_).isDefined)
      .map(_.getName)
      .collect {
        case path if path.startsWith(fontsDirectoryName) && path.toLowerCase(Locale.ROOT).endsWith(".flf") =>
          path.substring(fontsDirectoryName.length, path.length - 4)
      }
      .toVector

}
