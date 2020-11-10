package com.colofabrix.scala.figlet4s

import cats._
import cats.effect._
import com.colofabrix.scala.figlet4s.errors._
import scala.util._

/**
 * Figlet4s user interfaces that wraps results inside [[scala.Either]].
 *
 * The user interface provided by this package all return their result wrapped in [[scala.Either]] and no exceptions are
 * thrown. Errors and exceptions are stored as values in the `Left()` side of the [[scala.Either]].
 *
 * A simple example of using the `either` package to interact with Figlet4s is:
 *
 * {{{
 * import com.colofabrix.scala.figlet4s.either._
 *
 * val result =
 *   for {
 *     builder <- Figlet4s.builderF()             // Obtain an options builder
 *     figure  <- builder.render("Hello, World!") // Render a text into a FIGure
 *     lines   <- figure.asSeq()                  // Store the FIGure as lines in a variable
 *   } yield lines
 *
 * // Interpreting the result
 * result match {
 *   case Left(error)  => println(s"Error while working with FIGlet: $$error")
 *   case Right(value) => value.foreach(println)
 * }
 * }}}
 *
 * or the Figlet4s client can be used directly, without the mediation of the OptionsBuilder, with explicit management of
 * the [[scala.Either]] value:
 *
 * {{{
 * import cats.implicits._
 * import com.colofabrix.scala.figlet4s.options._
 *
 * // Load a font and choose some settings
 * val fontE           = Figlet4s.loadFontInternal("alligator")
 * val maxWidthE       = 120.asRight
 * val layoutE         = HorizontalLayout.HorizontalFitting.asRight
 * val printDirectionE = PrintDirection.LeftToRight.asRight
 *
 * // Build the render options, using cat's .mapN()
 * val optionsE =
 *   (fontE, maxWidthE, layoutE, printDirectionE)
 *     .mapN(RenderOptions(_, _, _, _))
 *
 * // Render a string into a FIGure
 * val figure = optionsE.map { options =>
 *   Figlet4s.renderString("Hello, World!", options)
 * }
 *
 * // Interpreting the result
 * result match {
 *   case Left(error)  => println(s"Error while working with FIGlet: $$error")
 *   case Right(value) => value.foreach(println)
 * }
 * }}}
 */
package object either extends FIGureMixin with OptionsBuilderMixin {

  type FigletEither[+A] = Either[FigletException, A]

  /**
   * Sync instance for Either
   */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit private[either] val syncEither: Sync[FigletEither] = new Sync[FigletEither] {
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
        case fe: FigletException => Left(fe)
        case t: Throwable        => Left(FigletError(t))
      }

    // format: off
    def bracketCase[A, B](acquire: FigletEither[A])(use: A => FigletEither[B])(release: (A, ExitCase[Throwable]) => FigletEither[Unit]): FigletEither[B] =
      throw new NotImplementedError("Sync[FigletEither] does not support Bracket.bracketCase")
    // format: on

    def handleErrorWith[A](fa: FigletEither[A])(f: Throwable => FigletEither[A]): FigletEither[A] =
      fa match {
        case Left(value) => f(value)
        case Right(_)    => fa
      }
  }

  /**
   * Transforms the FigletResult into a Either capturing the first error in the Left side
   */
  implicit private[either] class FigletResultOps[E, A](private val self: FigletResult[A]) extends AnyVal {
    def asEither: FigletEither[A] = self.fold(e => Left(e.head), Right(_))
  }

  /**
   * Unsafely returns the value inside the FigletEither or throws an exception with the first error
   */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit private[either] class FigletEitherOps[A](private val self: FigletEither[A]) extends AnyVal {
    @throws(classOf[FigletException])
    def unsafeGet: A = self.fold(e => throw e, identity)
  }

}
