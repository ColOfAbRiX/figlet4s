package com.colofabrix.scala.figlet4s

import _root_.cats.effect._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.core.FigletSync

/**
 * Figlet4s user interfaces that wraps results inside Cats' IO.
 *
 * The user interface provided by this package all return their result wrapped in Cats' IO and no exceptions are thrown.
 * Errors and exceptions are stored inside Cats' IO and accessible vai the [[cats.MonadError]] interface.
 *
 * A simple example of using the `catsio` package to interact with Figlet4s is:
 *
 * {{{
 * import cats.effect._
 * import com.colofabrix.scala.figlet4s.catsio._
 *
 * for {
 *   builder <- Figlet4s.builder()              // Obtain an options builder
 *   figure  <- builder.render("Hello, World!") // Render a text into a FIGure
 *   _       <- figure.print()                  // Print the FIGure
 * } yield ExitCode.Success
 * }}}
 *
 * or the Figlet4s client can be used directly, without the mediation of the OptionsBuilder, with explicit management of
 * the [[cats.effect.IO]] value:
 *
 * {{{
 * import cats.effect._
 * import cats.implicits._
 * import com.colofabrix.scala.figlet4s.catsio._
 * import com.colofabrix.scala.figlet4s.options._
 *
 * val result =
 *   for {
 *     // Load a font, choose the layout and max width
 *     font          <- Figlet4s.loadFontInternal("alligator")
 *     maxWidth       = 120
 *     layout         = HorizontalLayout.HorizontalFitting
 *     printDir       = PrintDirection.LeftToRight
 *     justification  = Justification.FontDefault
 *     // Build the render options
 *     options        = RenderOptions(font, maxWidth, layout, printDir, justification)
 *     // Render the string into a FIGure
 *     figure        <- Figlet4s.renderString("Hello, World!", options)
 *   } yield figure
 *
 * // Interpreting the result
 * result
 *   .map(_.print())
 *   .handleError(error => IO(println(s"Error while working with FIGlet: $$error")))
 * }}}
 */
package object catsio extends FIGureMixin with OptionsBuilderMixin {

  /**
   * FigletSync instance for Cats' IO, bridging the Figlet4s effect port onto cats-effect
   */
  implicit private[catsio] val figletSyncIO: FigletSync[IO] =
    new FigletSync[IO] {

      def pure[A](a: A): IO[A] =
        IO.pure(a)

      def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] =
        fa.flatMap(f)

      def delay[A](thunk: => A): IO[A] =
        IO.blocking(thunk)

      def raiseError[A](e: Throwable): IO[A] =
        IO.raiseError(e)

      def handleErrorWith[A](fa: IO[A])(f: Throwable => IO[A]): IO[A] =
        fa.handleErrorWith(f)

      def withResource[R <: AutoCloseable, A](acquire: => R)(use: R => IO[A]): IO[A] =
        Sync[IO].bracket(IO.blocking(acquire))(use)(resource => IO.blocking(resource.close()))

    }

  /**
   * Transforms the FigletResult into a Cat's IO capturing the first error in IO
   */
  private[catsio] def toIO[A](result: FigletResult[A]): IO[A] =
    result.toEither.fold(IO.raiseError(_), IO(_))

}
