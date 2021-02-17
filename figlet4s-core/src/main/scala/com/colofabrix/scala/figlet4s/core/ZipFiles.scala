package com.colofabrix.scala.figlet4s.core

import cats.effect._
import com.colofabrix.scala.figlet4s.core.Resource._
import java.io._
import java.util.zip.ZipEntry

object ZipFiles {

  def zipEntries[F[_]: Sync](in: InputStream): F[Iterator[ZipEntry]] =
    withResource(new java.util.zip.ZipInputStream(in)) { zip =>
      Sync[F].delay {
        Iterator.continually(zip.getNextEntry).takeWhile(Option(_).isDefined)
      }
    }

  def isZipFile[F[_]: Sync](path: String): F[Boolean] =
    withResource(new BufferedInputStream(new FileInputStream(path))) { reader =>
      Sync[F].delay {
        val bytes = Iterator
          .continually(reader.read())
          .takeWhile(_ > -1)
          .take(4)
          .map(_.toByte)
          .toList

        zipMagicBytes.contains(bytes)
      }
    }

  private val zipMagicBytes: List[List[Byte]] = List(
    List(0x50, 0x4b, 0x03, 0x04),
    List(0x50, 0x4b, 0x05, 0x06),
    List(0x50, 0x4b, 0x07, 0x08),
  )

}
