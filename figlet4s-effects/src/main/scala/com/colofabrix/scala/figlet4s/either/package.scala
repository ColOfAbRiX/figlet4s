package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.core.FigletSync
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
 *     builder <- Figlet4s.builder()              // Obtain an options builder
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
 * import com.colofabrix.scala.figlet4s.either._
 * import com.colofabrix.scala.figlet4s.options._
 *
 * // Load a font and choose some settings
 * val fontE           = Figlet4s.loadFontInternal("alligator")
 * val maxWidthE       = 120.asRight
 * val layoutE         = HorizontalLayout.HorizontalFitting.asRight
 * val printDirectionE = PrintDirection.LeftToRight.asRight
 * val justificationE  = Justification.FontDefault.asRight
 *
 * // Build the render options, using cats' .mapN()
 * val optionsE =
 *   (fontE, maxWidthE, layoutE, printDirectionE, justificationE)
 *     .mapN(RenderOptions(_, _, _, _, _))
 *
 * // Render a string into a FIGure
 * val figureE = optionsE.flatMap(options => Figlet4s.renderString("Hello, World!", options))
 *
 * // Interpreting the result
 * figureE match {
 *   case Left(error)  => println(s"Error while working with FIGlet: $$error")
 *   case Right(value) => value.print()
 * }
 * }}}
 */
package object either extends FIGureMixin with OptionsBuilderMixin {

  type FigletEither[+A] = Either[FigletException, A]

  /**
   * FigletSync instance for Either: errors are captured in the Left side and resources are always closed
   */
  implicit private[either] val figletSyncEither: FigletSync[FigletEither] =
    new FigletSync[FigletEither] {

      def pure[A](x: A): FigletEither[A] =
        Right(x)

      def flatMap[A, B](fa: FigletEither[A])(f: A => FigletEither[B]): FigletEither[B] =
        fa.flatMap(f)

      def delay[A](thunk: => A): FigletEither[A] =
        Try(thunk).toEither.left.map(t => FigletError(t.getMessage, t): FigletException)

      def raiseError[A](t: Throwable): FigletEither[A] =
        t match {
          case fe: FigletException => Left(fe)
          case t: Throwable        => Left(FigletError(t.getMessage, t))
        }

      def handleErrorWith[A](fa: FigletEither[A])(f: Throwable => FigletEither[A]): FigletEither[A] =
        fa match {
          case Left(value) => f(value)
          case Right(_)    => fa
        }

      def withResource[R <: AutoCloseable, A](acquire: => R)(use: R => FigletEither[A]): FigletEither[A] =
        Try(acquire)
          .toEither
          .left
          .map { t =>
            FigletError(t.getMessage, t): FigletException
          }
          .flatMap { resource =>
            val used =
              Try(use(resource))
                .toEither
                .left
                .map(t => FigletError(t.getMessage, t): FigletException)
                .flatten

            val closed =
              Try(resource.close())
                .toEither
                .left
                .map(t => FigletError(t.getMessage, t): FigletException)

            (used, closed) match {
              case (Left(error), _)     => Left(error)
              case (Right(_), Left(ct)) => Left(ct)
              case (Right(value), _)    => Right(value)
            }
          }

    }

}
