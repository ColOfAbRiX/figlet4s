package com.colofabrix.scala.figlet4s

import cats._
import cats.effect._
import cats.effect.kernel.CancelScope
import scala.annotation.nowarn
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
   * Sync instance for Id for impure calculations.
   *
   * This provides a minimal Sync[Id] implementation that allows the unsafe package
   * to work with the CE3-based Figlet4sClient. Since Id cannot truly suspend effects,
   * this implementation executes everything immediately and throws exceptions on errors.
   */
  @nowarn("msg=private .* is never used|side-effecting nullary methods are discouraged")
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements", "org.wartremover.warts.Throw"))
  implicit private[unsafe] val syncId: Sync[Id] =
    new Sync[Id] {
      private val M: Monad[Id] = Monad[Id](using catsInstancesForId)

      // Monad methods
      def pure[A](x: A): Id[A] =
        M.pure(x)

      def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
        M.flatMap(fa)(f)

      def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] =
        M.tailRecM(a)(f)

      // MonadError methods
      def raiseError[A](e: Throwable): Id[A] =
        throw e

      def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
        try fa catch { case e: Throwable => f(e) }

      // Clock methods
      def monotonic: Id[FiniteDuration] =
        FiniteDuration(System.nanoTime(), scala.concurrent.duration.NANOSECONDS)

      def realTime: Id[FiniteDuration] =
        FiniteDuration(System.currentTimeMillis(), scala.concurrent.duration.MILLISECONDS)

      // Unique methods
      override def unique: Id[Unique.Token] =
        new Unique.Token

      // Sync-specific methods
      def suspend[A](hint: Sync.Type)(thunk: => A): Id[A] =
        thunk

      // MonadCancel methods
      def canceled: Id[Unit] =
        ()

      def forceR[A, B](fa: Id[A])(fb: Id[B]): Id[B] = {
        try fa catch { case _: Throwable => () }
        fb
      }

      def onCancel[A](fa: Id[A], fin: Id[Unit]): Id[A] =
        fa

      def uncancelable[A](body: Poll[Id] => Id[A]): Id[A] = {
        val poll = new Poll[Id] { def apply[B](fa: Id[B]): Id[B] = fa }
        body(poll)
      }

      def rootCancelScope: CancelScope =
        CancelScope.Uncancelable

      // GenSpawn methods - not truly implementable for Id
      def never[A]: Id[A] =
        throw new NotImplementedError("Sync[Id] does not support GenSpawn.never")

      def cede: Id[Unit] =
        ()

      def start[A](fa: Id[A]): Id[Fiber[Id, Throwable, A]] =
        throw new NotImplementedError("Sync[Id] does not support GenSpawn.start")
        throw new NotImplementedError("Sync[Id] does not support GenSpawn.racePair")
    }

}
