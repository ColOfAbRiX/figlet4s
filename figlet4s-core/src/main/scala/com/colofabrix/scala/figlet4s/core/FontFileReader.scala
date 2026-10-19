package com.colofabrix.scala.figlet4s.core

import com.colofabrix.scala.figlet4s.errors._
import java.io._
import java.nio._
import java.nio.charset._
import java.util.zip._
import scala.io._
import scala.util.control.NonFatal

private[figlet4s] object FontFileReader {

  final private case class BytesHolder(bytes: Option[Array[Byte]]) extends AutoCloseable {

    override def close(): Unit = ()

  }

  /**
   * Reads a font file, even zipped, from file system and passes the reader to a function
   */
  def read[F[_]: FigletSync, A](path: String, codec: Codec)(f: (File, BufferedSource) => F[A]): F[A] = {
    val fs = FigletSync[F]

    val opened: F[BufferedInputStream] =
      try {
        fs.pure(new BufferedInputStream(new FileInputStream(path)))
      } catch {
        case NonFatal(t) => fs.raiseError(new FigletLoadingError(t.getMessage, t))
      }

    fs.flatMap(opened) { inputStream =>
      val file = new File(path)
      read(inputStream, codec)(f(file, _))
    }
  }

  /**
   * Reads a font file from the internal library resources (files or JAR).
   */
  def readInternal[F[_], A](path: String, codec: Codec)(f: (File, BufferedSource) => F[A])(
      implicit F: FigletSync[F],
  ): F[A] =
    F.withResource(acquireFontBytes(path)) { h =>
      h.bytes match {
        case None =>
          F.raiseError(new FigletLoadingError(s"Cannot read the internal font '$path'"))
        case Some(bytes) =>
          F.flatMap(sourceAsInputStream(bytes, codec)) { source =>
            F.flatMap(F.delay(new File(this.getClass.getClassLoader.getResource(path).getPath))) { file =>
              f(file, source)
            }
          }
      }
    }

  //  Support  //

  private def sourceAsInputStream[F[_]](bytes: Array[Byte], codec: Codec)(
      implicit F: FigletSync[F],
  ): F[BufferedSource] =
    F.delay {
      implicit val actualCodec: Codec = if (isValidUtf8(bytes)) Codec.UTF8 else codec

      val byteSource = Source.fromInputStream(new BufferedInputStream(new ByteArrayInputStream(bytes)))
      tapSource(byteSource)
    }

  private[core] def read[F[_], A](bif: => BufferedInputStream, codec: Codec)(f: BufferedSource => F[A])(
      implicit F: FigletSync[F],
  ): F[A] = {
    F.withResource(bif) { inputStream =>
      implicit val c: Codec = codec
      F.flatMap(isZipFile(inputStream)) { isZip =>
        F.flatMap(if (isZip) readZip(inputStream) else readText(inputStream)) { stream =>
          f(Source.fromInputStream(stream))
        }
      }
    }
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

  private def readText[F[_]](is: BufferedInputStream)(implicit F: FigletSync[F]): F[BufferedInputStream] =
    F.delay(is)

  private def isZipFile[F[_]](is: BufferedInputStream)(implicit F: FigletSync[F]): F[Boolean] =
    F.delay {
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

  private def readZip[F[_]](is: BufferedInputStream)(implicit F: FigletSync[F]): F[BufferedInputStream] =
    F.flatMap(
      F.delay {
        val zipInputStream = new ZipInputStream(is)
        Option(zipInputStream.getNextEntry).map(_ => new BufferedInputStream(zipInputStream))
      },
    ) {
      case Some(stream) =>
        F.pure(stream)
      case None =>
        F.raiseError(new FigletLoadingError("Cannot read font file from ZIP"))
    }

  // See: https://www.wikiwand.com/en/List_of_file_signatures
  private val zipMagicBytes: List[List[Byte]] = List(
    List(0x50, 0x4b, 0x03, 0x04),
    List(0x50, 0x4b, 0x05, 0x06), // Empty archive
    List(0x50, 0x4b, 0x07, 0x08), // Spanned archive
  )

}
