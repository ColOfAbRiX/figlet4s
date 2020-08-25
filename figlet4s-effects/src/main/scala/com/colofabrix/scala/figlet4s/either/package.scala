package com.colofabrix.scala.figlet4s

import cats._
import cats.effect._
import com.colofabrix.scala.figlet4s.api._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import scala.util._

package object either {

  type FigletEither[+A] = Either[FigletError, A]

  //  OptionsBuilder  //

  implicit class OptionsBuilderOps(val self: OptionsBuilder) extends OptionsBuilderAPI[FigletEither] {
    private lazy val buildOptions = self.compile[FigletEither]

    /** The text to render */
    def text: FigletEither[String] = buildOptions.map(_.text)

    /** Renders the text into a FIGure */
    def render(): FigletEither[FIGure] =
      for {
        options  <- options
        text     <- text
        rendered <- InternalAPI.renderString[FigletEither](text, options)
      } yield rendered

    /** Renders a given text into a FIGure */
    def render(text: String): FigletEither[FIGure] =
      self
        .text(text)
        .render()

    /** Returns the render options */
    def options: FigletEither[RenderOptions] =
      for {
        font             <- builtFont
        maxWidth         <- builtMaxWidth
        horizontalLayout <- buildOptions.map(_.horizontalLayout)
        printDirection   <- buildOptions.map(_.printDirection)
        justification    <- buildOptions.map(_.justification)
      } yield {
        RenderOptions(font, maxWidth, horizontalLayout, printDirection, justification)
      }

    //  Support  //

    private def builtFont: FigletEither[FIGfont] =
      for {
        optionFont <- buildOptions.map(_.font.map(_.asEither))
        font       <- optionFont.getOrElse(builtDefaultFont)
      } yield font

    private def builtDefaultFont: FigletEither[FIGfont] =
      InternalAPI.loadFontInternal[FigletEither]().flatMap(_.asEither)

    private def builtMaxWidth: FigletEither[Int] =
      for {
        optionMaxWidth <- buildOptions.map(_.maxWidth)
        maxWidth       <- Right(optionMaxWidth.getOrElse(Int.MaxValue))
      } yield maxWidth
  }

  //  FIGure  //

  implicit class FIGureOps(val figure: FIGure) extends FIGureAPI[FigletEither] {
    /** Apply a function to each line of the FIGure */
    def foreachLine[A](f: String => A): FigletEither[Unit] = Right {
      figure.cleanLines.foreach(_.foreach(f))
    }

    /** Print the FIGure */
    def print(): FigletEither[Unit] =
      figure.foreachLine(println)

    /** The figure as a Vector of String */
    def asVector(): FigletEither[Vector[String]] = Right {
      figure.cleanLines.map(_.value.mkString("\n"))
    }

    /** The figure as single String */
    def asString(): FigletEither[String] =
      asVector().map(_.mkString("\n"))
  }

  //  FigletResult  //

  /** Transforms the FigletResult into a Either capturing the first error in the Left side */
  implicit private[either] class FigletResultOps[E, A](val self: FigletResult[A]) extends AnyVal {
    def asEither: FigletEither[A] = self.fold(e => Left(e.head), Right(_))
  }

  //  Sync[FigletEither]  //

  /**
   * Sync instance for Either
   */
  @SuppressWarnings(Array("org.wartremover.warts.Throw", "org.wartremover.warts.ImplicitParameter"))
  implicit val syncEither: Sync[FigletEither] = new Sync[FigletEither] {
    import cats.implicits._

    private val M: Monad[FigletEither] = Monad[FigletEither](catsStdInstancesForEither)

    def pure[A](x: A): FigletEither[A] =
      M.pure(x)

    def flatMap[A, B](fa: FigletEither[A])(f: A => FigletEither[B]): FigletEither[B] =
      M.flatMap(fa)(f)

    def tailRecM[A, B](a: A)(f: A => FigletEither[Either[A, B]]): FigletEither[B] =
      M.tailRecM(a)(f)

    def suspend[A](thunk: => FigletEither[A]): FigletEither[A] =
      Try(thunk)
        .toEither
        .flatMap(identity)
        .leftFlatMap(raiseError)

    def raiseError[A](t: Throwable): FigletEither[A] =
      t match {
        case fe: FigletError => Left(fe)
        case t: Throwable    => Left(FigletException(t))
      }

    // format: off
    def bracketCase[A, B](acquire: FigletEither[A])(use: A => FigletEither[B])(release: (A, ExitCase[Throwable]) => FigletEither[Unit]): FigletEither[B] =
      throw new NotImplementedError("Sync[Either[FigletError, A]] doesn not support Bracket.bracketCase")
    // format: on

    def handleErrorWith[A](fa: FigletEither[A])(f: Throwable => FigletEither[A]): FigletEither[A] =
      fa match {
        case Left(value) => f(value)
        case Right(_)    => fa
      }
  }

}
