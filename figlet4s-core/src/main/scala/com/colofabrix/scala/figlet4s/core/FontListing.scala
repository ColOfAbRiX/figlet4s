package com.colofabrix.scala.figlet4s.core

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import java.io._
import java.net._
import java.nio.file._
import java.util.zip._

private[figlet4s] object FontListing {

  /**
   * The list of available internal fonts
   *
   * @tparam F A higher-kinded type for which there is a [[cats.effect.Sync]] instance
   * @return The collection of names of FIGfonts shipped with this library
   */
  def listInternalFonts[F[_]: Sync]: F[Seq[String]] = {
    val resources =
      getClass
        .getProtectionDomain
        .getCodeSource
        .getLocation
        .toURI

    val file = new File(resources)

    if (file.isDirectory) {
      val fontsPath = Paths.get(resources.resolve("fonts"))
      fromDirectory(fontsPath).map(cleanAbsolutePath(fontsPath))
    } else if (file.getName.toLowerCase.endsWith(".jar"))
      fromJar(resources).map(cleanAbsolutePath(Paths.get("fonts")))
    else
      Sync[F].raiseError {
        FigletError("Could not determine the type of artifacts where I can find the fonts")
      }
  }

  //  Support  //

  private def fromDirectory[F[_]: Sync](startPath: Path): F[Vector[Path]] = {
    def br = new BufferedReader(new InputStreamReader(startPath.toUri.toURL.openStream()))
    Braket.withResource(br) { reader =>
      Sync[F].defer(listDirectory(startPath, reader))
    }
  }

  private def listDirectory[F[_]: Sync](startPath: Path, reader: BufferedReader): F[Vector[Path]] =
    Iterator
      .continually(reader.readLine)
      .takeWhile(Option(_).isDefined)
      .toVector
      .flatTraverse[F, Path] { name =>
        val path = startPath.resolve(name)
        if (Files.isDirectory(path)) fromDirectory(path) else Sync[F].pure(Vector(path))
      }

  private def fromJar[F[_]: Sync](resources: URI): F[Vector[Path]] = {
    def zis = new ZipInputStream(resources.toURL.openStream)
    Braket.withResource(zis) { zip =>
      Sync[F].delay(listJar(zip))
    }
  }

  private def listJar[F[_]: Sync](reader: ZipInputStream): Vector[Path] =
    Iterator
      .continually(reader.getNextEntry)
      .takeWhile(Option(_).isDefined)
      .map(zipEntry => Paths.get(zipEntry.getName))
      .withFilter(path => path.toString.startsWith("fonts") && path.toString.endsWith(".flf"))
      .toVector

  private def cleanAbsolutePath(basePath: Path)(paths: Vector[Path]): Seq[String] = {
    val pathPrefix = basePath.toString + FileSystems.getDefault().getSeparator()
    paths
      .map {
        _.toString()
          .replace(pathPrefix, "")
          .replace(".flf", "")
      }
      .filterNot(_.endsWith(".flc"))
  }

}
