package com.colofabrix.scala.figlet4s.unsafe

import cats._
import cats.effect._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.figfont._
import scala.annotation.tailrec

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation
 */
@SuppressWarnings(Array("org.wartremover.warts.Throw"))
object Figlet4s {

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

  //  Support  //

  implicit private val syncId: Sync[Id] = new Sync[Id] {
    import scala.util._

    def pure[A](x: A): Id[A] =
      x

    def suspend[A](thunk: => Id[A]): Id[A] =
      pure(thunk)

    def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
      f(fa)

    @tailrec
    def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] =
      f(a) match {
        case Left(value)  => tailRecM(value)(f)
        case Right(value) => value
      }

    def bracketCase[A, B](resource: Id[A])(use: A => Id[B])(release: (A, ExitCase[Throwable]) => Id[Unit]): Id[B] =
      Try(use(resource)) match {
        case Failure(error)  =>
          release(resource, ExitCase.Error(error))
          throw error
        case Success(result) =>
          release(resource, ExitCase.Completed)
          result
      }

    def raiseError[A](e: Throwable): Id[A] =
      throw e

    def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
      fa
  }
}
