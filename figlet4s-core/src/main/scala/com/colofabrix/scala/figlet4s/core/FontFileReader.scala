package com.colofabrix.scala.figlet4s.core

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import java.io._
import java.util.zip._
import scala.io._

private[figlet4s] object FontFileReader {

  def read[F[_]: Sync, A](path: String, codec: Codec)(f: (File, BufferedSource) => F[A]): F[A] = {
    def bs = new BufferedInputStream(new FileInputStream(path))
    val file = new File(path)
    read(bs, codec)(f(file, _))
  }

  def readInternal[F[_]: Sync, A](path: String, codec: Codec)(f: (File, BufferedSource) => F[A]): F[A] = {
    def is = Source.fromInputStream(this.getClass.getClassLoader.getResourceAsStream(path))(codec)
    val file = new File(this.getClass.getClassLoader.getResource(path).toURI())
    Braket.withResource(tapSource(is))(f(file, _))
  }

  //  Support  //

  private[core] def read[F[_]: Sync, A](bif: => BufferedInputStream, codec: Codec)(f: BufferedSource => F[A]): F[A] =
    Braket.withResource(bif) { inputStream =>
      for {
        isZip  <- isZipFile(inputStream)
        stream <- if (isZip) readZip(inputStream) else readText(inputStream)
        result <- f(Source.fromInputStream(stream)(codec))
      } yield result
    }

  // This forces the BufferedSource to evaluate and to raise errors early on
  private def tapSource(bs: BufferedSource): BufferedSource = {
    val tester = bs.bufferedReader()
    tester.mark(1)
    bs
  }

  private def readText[F[_]: Sync, R <: BufferedInputStream, A](is: BufferedInputStream): F[BufferedInputStream] =
    Sync[F].delay(is)

  private def isZipFile[F[_]: Sync](is: BufferedInputStream): F[Boolean] =
    Sync[F].delay {
      is.mark(4)

      val headerBytes =
        Iterator
          .continually(is.read())
          .takeWhile(_ > -1)
          .take(4)
          .toList
          .map(_.toByte)

      is.reset()
      zipMagicBytes.contains(headerBytes)
    }

  private def readZip[F[_]: Sync, A](is: BufferedInputStream): F[BufferedInputStream] =
    Sync[F].delay {
      val zipInputStream = new ZipInputStream(is)
      Option(zipInputStream.getNextEntry) match {
        case Some(_) => Sync[F].pure(new BufferedInputStream(zipInputStream))
        case None    => Sync[F].raiseError[BufferedInputStream](new FigletLoadingError("Cannot read font file from ZIP"))
      }
    }.flatten

  // See: https://www.wikiwand.com/en/List_of_file_signatures
  private val zipMagicBytes: List[List[Byte]] = List(
    List(0x50, 0x4b, 0x03, 0x04),
    List(0x50, 0x4b, 0x05, 0x06), // Empty archive
    List(0x50, 0x4b, 0x07, 0x08), // Spanned archive
  )
}
