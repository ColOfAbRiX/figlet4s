package com.colofabrix.scala.figlet4s.core

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import java.io._
import java.net._
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

    if (file.isDirectory)
      internalFontsFromDirectory(resources).map(_.toSeq)
    else if (file.getName.toLowerCase.endsWith(".jar"))
      internalFontsFromJar(resources).map(_.toSeq)
    else
      Sync[F].raiseError {
        FigletError("Could not determine the type of artifacts to open to find the fonts")
      }
  }

  //  Support  //

  private def dirReaderStream(resources: URI): BufferedReader =
    new BufferedReader(new InputStreamReader(resources.resolve("fonts").toURL.openStream()))

  private def internalFontsFromDirectory[F[_]: Sync](resources: URI): F[List[String]] =
    Braket.withResource(dirReaderStream(resources)) { reader =>
      Sync[F].delay {
        Iterator
          .continually(reader.readLine)
          .takeWhile(Option(_).isDefined)
          .withFilter(path => path.endsWith(".flf"))
          .map(_.replace(".flf", ""))
          .toList
      }
    }

  private def jarReaderStream(resources: URI): ZipInputStream =
    new ZipInputStream(resources.toURL.openStream)

  private def internalFontsFromJar[F[_]: Sync](resources: URI): F[List[String]] =
    Braket.withResource(jarReaderStream(resources)) { zip =>
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

}