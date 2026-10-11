package com.colofabrix.scala.figlet4s.core

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import java.io._
import java.nio._
import java.nio.charset._
import java.util.zip._
import scala.io._

private[figlet4s] object FontFileReader {

  final private case class BytesHolder(bytes: Option[Array[Byte]]) extends AutoCloseable {

    override def close(): Unit = ()

  }

  /**
   * Reads a font file, even zipped, from file system and passes the reader to a function
   */
  def read[F[_]: Sync, A](path: String, codec: Codec)(f: (File, BufferedSource) => F[A]): F[A] = {
    def bs   = new BufferedInputStream(new FileInputStream(path))
    def file = new File(path)
    read(bs, codec)(f(file, _))
  }

  /**
   * Reads a font file from the internal library resources (files or JAR).
   */
  def readInternal[F[_]: Sync, A](path: String, codec: Codec)(f: (File, BufferedSource) => F[A]): F[A] = {
    Braket.withResource(acquireFontBytes(path)) { h =>
      for {
        bytes  <- Sync[F].fromOption(h.bytes, new FigletLoadingError(s"Cannot read the internal font '$path'"))
        source <- sourceAsInputStream(bytes, codec)
        file   <- Sync[F].delay(new File(this.getClass.getClassLoader.getResource(path).getPath))
        result <- f(file, source)
      } yield result
    }
  }

  //  Support  //

  private def sourceAsInputStream[F[_]: Sync, A](bytes: Array[Byte], codec: Codec): F[BufferedSource] =
    Sync[F].delay {
      implicit val actualCodec: Codec = if (isValidUtf8(bytes)) Codec.UTF8 else codec

      val byteSource = Source.fromInputStream(new BufferedInputStream(new ByteArrayInputStream(bytes)))
      tapSource(byteSource)
    }

  private[core] def read[F[_]: Sync, A](bif: => BufferedInputStream, codec: Codec)(f: BufferedSource => F[A]): F[A] =
    Braket.withResource(bif) { inputStream =>
      implicit val c: Codec = codec
      for {
        isZip  <- isZipFile(inputStream)
        stream <- if (isZip) readZip(inputStream) else readText(inputStream)
        result <- f(Source.fromInputStream(stream))
      } yield result
    }

  // This forces the BufferedSource to evaluate and to raise errors early on
  private def tapSource(bs: BufferedSource): BufferedSource = {
    val tester = bs.bufferedReader()
    tester.mark(1)
    bs
  }

  private def acquireFontBytes(path: String): BytesHolder =
    BytesHolder {
      Option(this.getClass.getClassLoader.getResourceAsStream(path)).map { is =>
        try is.readAllBytes()
        finally is.close()
      }
    }

  private def isValidUtf8(bytes: Array[Byte]): Boolean =
    try {
      val decoder =
        Codec
          .UTF8.decoder
          .onMalformedInput(CodingErrorAction.REPORT)
          .onUnmappableCharacter(CodingErrorAction.REPORT)
      val _ = decoder.decode(ByteBuffer.wrap(bytes))

      true
    } catch {
      case _: CharacterCodingException => false
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
        case None => Sync[F].raiseError[BufferedInputStream](new FigletLoadingError("Cannot read font file from ZIP"))
      }
    }.flatten

  // See: https://www.wikiwand.com/en/List_of_file_signatures
  private val zipMagicBytes: List[List[Byte]] = List(
    List(0x50, 0x4b, 0x03, 0x04),
    List(0x50, 0x4b, 0x05, 0x06), // Empty archive
    List(0x50, 0x4b, 0x07, 0x08), // Spanned archive
  )

}
