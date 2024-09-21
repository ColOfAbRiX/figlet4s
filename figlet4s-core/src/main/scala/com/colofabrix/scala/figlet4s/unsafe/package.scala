package com.colofabrix.scala.figlet4s

import cats._
import cats.effect._
import cats.effect.kernel.CancelScope
import com.colofabrix.scala.figlet4s.errors._

import scala.concurrent.duration.FiniteDuration

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
   * Sync instance for Id for impure calculations
   */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit private[unsafe] val syncId: Sync[Id] = new Sync[Id] {
    implicit private val M: Monad[Id] = Monad[Id](catsInstancesForId)

    override def pure[A](x: A): Id[A] =
      M.pure(x)

    override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
      M.flatMap(fa)(f)

    override def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] =
      M.tailRecM(a)(f)

    override def raiseError[A](e: Throwable): Id[A] =
      throw e

    override def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
      throw new NotImplementedError("Sync[Id] does not support ApplicativeError.handleErrorWith")

    override def suspend[A](hint: Sync.Type)(thunk: => A): Id[A] =
      M.pure(thunk)

    override def monotonic: Id[FiniteDuration] =
      M.pure(FiniteDuration(System.nanoTime(), "nanos"))

    override def realTime: Id[FiniteDuration] =
      M.pure(FiniteDuration(System.currentTimeMillis(), "millis"))

    override def rootCancelScope: CancelScope =
      CancelScope.Cancelable

    override def forceR[A, B](fa: Id[A])(fb: Id[B]): Id[B] =
      M.flatMap(fa)(_ => fb)

    override def uncancelable[A](body: Poll[Id] => Id[A]): Id[A] =
      body.apply(new Poll[Id] {
        override def apply[B](fb: Id[B]): Id[B] = fb
      })

    override def canceled: Id[Unit] =
      M.pure(())

    override def onCancel[A](fa: Id[A], fin: Id[Unit]): Id[A] =
      M.flatMap(fin)(_ => fa)
  }

  /**
   * Unsafely returns the value inside the FigletResult or throws an exception with the first error
   */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit private[unsafe] class FigletResultOps[E, A](private val self: FigletResult[A]) extends AnyVal {
    @throws(classOf[FigletException])
    def unsafeGet: A = self.fold(e => throw e.head, identity)
  }

}
