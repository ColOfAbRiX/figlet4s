package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.core.FigletSync
import com.colofabrix.scala.figlet4s.errors._

/**
 * Figlet4s user interfaces that do not use effects
 *
 * This package allow a user to directly access the results of calls of Figlet4s without having to use the patterns of
 * functional programming like [[scala.Either]] or Cats' IO. On the other hand, the calls that work with files, like
 * loading a FIGfont, can throw exception as a result of errors.
 *
 * A simple example of using the `unsafe` package to interact with Figlet4s is:
 *
 * {{{
 * import com.colofabrix.scala.figlet4s.unsafe._
 *
 * // 1. Obtain an options builder
 * val builder = Figlet4s.builder()
 *
 * // 2. In this example we use the default configuration
 *
 * // 3. Render a text into a FIGure
 * val figure = builder.render("Hello, World!")
 *
 * // Do something with the FIGure
 * figure.print()
 * }}}
 *
 * or the Figlet4s client can be used directly, without the mediation of the OptionsBuilder:
 *
 * {{{
 * import com.colofabrix.scala.figlet4s.unsafe._
 * import com.colofabrix.scala.figlet4s.options._
 *
 * // Load a font, choose the layout and max width
 * val font           = Figlet4s.loadFontInternal("calgphy2")
 * val maxWidth       = 120
 * val layout         = HorizontalLayout.HorizontalFitting
 * val printDirection = PrintDirection.LeftToRight
 * val justification  = Justification.FontDefault
 *
 * // Build the render options
 * val options = RenderOptions(font, maxWidth, layout, printDirection, justification)
 *
 * // Render a string into a FIGure
 * val figure = Figlet4s.renderString("Hello, World!", options)
 *
 * // Print the FIGure
 * figure.print()
 * }}}
 *
 * If you want to manage effects in a purely functional fashion see the additional dependency figlet4s-effects
 */
package object unsafe extends OptionsBuilderMixin with FIGureMixin {

  /**
   * Identity effect: values are evaluated eagerly and errors are thrown
   */
  type Id[A] = A

  /**
   * FigletSync instance for Id, for impure calculations.
   *
   * Since Id cannot suspend effects, this implementation executes everything immediately and throws exceptions on
   * errors.
   */
  implicit private[figlet4s] val figletSyncId: FigletSync[Id] =
    new FigletSync[Id] {

      def pure[A](a: A): Id[A] =
        a

      def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
        f(fa)

      def delay[A](thunk: => A): Id[A] =
        thunk

      @SuppressWarnings(Array("org.wartremover.warts.Throw"))
      def raiseError[A](e: Throwable): Id[A] =
        throw e

      def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
        try fa
        catch { case e: Throwable => f(e) }

      @SuppressWarnings(Array("org.wartremover.warts.Throw", "org.wartremover.warts.Var"))
      def withResource[R <: AutoCloseable, A](acquire: => R)(use: R => Id[A]): Id[A] = {
        var resource: Option[R]             = None
        var closeFailure: Option[Throwable] = None

        val outcome: Either[Throwable, A] =
          try {
            val r = acquire
            resource = Some(r)
            Right(use(r))
          } catch {
            case t: Throwable => Left(t)
          } finally {
            resource.foreach { r =>
              try {
                r.close()
              } catch {
                case t: Throwable => closeFailure = Some(t)
              }
            }
          }

        (outcome, closeFailure) match {
          case (Left(t), Some(closeError)) =>
            val error = FigletLoadingError(t.getMessage, t)
            error.addSuppressed(closeError)
            throw error
          case (Left(t), None) =>
            throw FigletLoadingError(t.getMessage, t)
          case (Right(value), Some(closeError)) =>
            throw closeError
          case (Right(value), None) =>
            value
        }
      }

    }

}
