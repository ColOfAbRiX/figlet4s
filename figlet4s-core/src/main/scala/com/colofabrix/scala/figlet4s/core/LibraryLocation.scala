package com.colofabrix.scala.figlet4s.core

import cats.effect.Sync
import java.io.File
import java.net.URI
import java.nio.file.FileSystems
import java.util.Locale

/**
 * Position of the current library, if inside a JAR or in the FileSystem
 */
sealed private[figlet4s] trait LibraryLocation extends Product with Serializable

private[figlet4s] object LibraryLocation {

  final case class Jar(location: URI)                           extends LibraryLocation
  final case class FileSystem(location: URI, separator: String) extends LibraryLocation
  case object Unknown                                           extends LibraryLocation

  /**
   * Discovers where the current code is located
   */
  def discover[F[_]: Sync]: F[LibraryLocation] =
    Sync[F].delay {
      val resources =
        Option(
          getClass
            .getProtectionDomain
            .getCodeSource
            .getLocation
            .toURI,
        )

      resources match {
        case Some(resources) =>
          val file = new File(resources)
          if (file.isDirectory)
            LibraryLocation.FileSystem(resources, FileSystems.getDefault.getSeparator)
          else if (file.getName.toLowerCase(Locale.ROOT).endsWith(".jar"))
            LibraryLocation.Jar(resources)
          else
            LibraryLocation.Unknown
        case None =>
          LibraryLocation.Unknown
      }
    }

}
