package com.colofabrix.scala.figlet4s

import cats._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._

package object unsafe {

  //  OptionsBuilder  //

  implicit class OptionsBuilderOps(val self: OptionsBuilder) extends OptionsBuilderClientAPI[Id] {
    private lazy val buildOptions = self.compile[Id]

    /** The text to render */
    def text: String = buildOptions.text

    /** Renders a given text as a FIGure and throws exceptions in case of errors */
    def render(): FIGure =
      Figlet4s.renderString(buildOptions.text, options)

    /** Renders a given text into a FIGure */
    def render(text: String): FIGure =
      self
        .text(text)
        .render()

    /** Builds an instance of RenderOptions */
    def options: RenderOptions = {
      val font =
        buildOptions
          .font
          .getOrElse(Figlet4s.loadFontInternal().validNec)
          .unsafeGet

      val maxWidth =
        buildOptions
          .maxWidth
          .getOrElse(Int.MaxValue)

      RenderOptions(
        font,
        maxWidth,
        buildOptions.horizontalLayout,
        buildOptions.printDirection,
        buildOptions.justification,
      )
    }
  }

  //  FIGure  //

  implicit class FIGureOps(val self: FIGure) extends FIGureClientAPI[Id] {
    /** Apply a function to each line of the FIGure */
    def foreachLine[A](f: String => A): Unit =
      self.cleanLines.foreach(_.foreach(f))

    /** Print the FIGure */
    def print(): Unit =
      self.foreachLine(println)

    /** The figure as a Vector of String */
    def asVector(): Vector[String] =
      self.cleanLines.map(_.value.mkString("\n"))

    /** The figure as single String */
    def asString(): String =
      asVector().mkString("\n")
  }

  //  FigletResult  //

  /** Unsafely returns the value inside the FigletResult or throws an exception with the first error */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit private[unsafe] class FigletResultOps[E, A](val self: FigletResult[A]) extends AnyVal {
    def unsafeGet: A = self.fold(e => throw e.head, identity)
  }

  //  Sync[Id]  //

  /**
   * Sync instance for Id for impure calculations
   */
  @SuppressWarnings(Array("org.wartremover.warts.Throw", "org.wartremover.warts.ImplicitParameter"))
  implicit val syncId: Sync[Id] = new Sync[Id] {
    private val M: Monad[Id] = Monad[Id](catsInstancesForId)

    def pure[A](x: A): Id[A] =
      M.pure(x)

    def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
      M.flatMap(fa)(f)

    def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] =
      M.tailRecM(a)(f)

    def suspend[A](thunk: => Id[A]): Id[A] =
      thunk

    def raiseError[A](e: Throwable): Id[A] =
      throw e

    def bracketCase[A, B](resource: Id[A])(use: A => Id[B])(release: (A, ExitCase[Throwable]) => Id[Unit]): Id[B] =
      throw new NotImplementedError("Sync[Id] doesn not support Bracket.bracketCase")

    def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
      throw new NotImplementedError("Sync[Id] doesn not support ApplicativeError.handleErrorWith")
  }
}
