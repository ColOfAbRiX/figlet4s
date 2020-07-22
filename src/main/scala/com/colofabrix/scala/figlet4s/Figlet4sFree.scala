package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.figfont._

import cats.free.Free
import cats.free.Free.liftF
import cats.arrow.FunctionK
import cats.{ Id, ~> }
import cats.Monad
import cats.effect.IO

object Figlet4sFree {

  sealed trait Figlet4sA[A]                                           extends Serializable with Product
  final case object InternalFonts                                     extends Figlet4sA[Vector[String]]
  final case class RenderString(text: String, options: RenderOptions) extends Figlet4sA[FIGure]
  final case class LoadFontInternal(name: String)                     extends Figlet4sA[FigletResult[FIGfont]]
  final case class LoadFont(path: String, encoding: String)           extends Figlet4sA[FigletResult[FIGfont]]

  type Figlet4sAction[A] = Free[Figlet4sA, A]

  def internalFonts: Figlet4sAction[Vector[String]] =
    liftF[Figlet4sA, Vector[String]](InternalFonts)

  def renderString(text: String, options: RenderOptions): Figlet4sAction[FIGure] =
    liftF[Figlet4sA, FIGure](RenderString(text, options))

  def loadFontInternal(name: String): Figlet4sAction[FigletResult[FIGfont]] =
    liftF[Figlet4sA, FigletResult[FIGfont]](LoadFontInternal(name))

  def loadFont(path: String, encoding: String): Figlet4sAction[FigletResult[FIGfont]] =
    liftF[Figlet4sA, FigletResult[FIGfont]](LoadFont(path, encoding))

  implicit class Figlet4sActionOps[A](val self: Figlet4sAction[A]) {
    def runF[F[_]: Monad](
        implicit
        compiler: Figlet4sA ~> F,
    ): F[A] =
      self.foldMap(compiler)
  }
}

object Figlet4sFreeId {
  import Figlet4sFree._

  implicit val idCompiler: Figlet4sA ~> Id =
    new FunctionK[Figlet4sA, Id] {
      def apply[A](fa: Figlet4sA[A]): Id[A] = fa match {
        case InternalFonts               => Figlet4s.internalFonts
        case LoadFont(path, encoding)    => Figlet4s.loadFont(path, encoding)
        case LoadFontInternal(name)      => Figlet4s.loadFontInternal(name)
        case RenderString(text, options) => Figlet4s.renderString(text, options)
      }
    }

  implicit class Figlet4sActionOps[A](val self: Figlet4sAction[A]) {
    def render: A = self.foldMap(idCompiler)
  }
}

object Figlet4sFreeIO {
  import Figlet4sFree._

  implicit val idCompiler: Figlet4sA ~> IO =
    new FunctionK[Figlet4sA, IO] {
      def apply[A](fa: Figlet4sA[A]): IO[A] = fa match {
        case InternalFonts               => IO(Figlet4s.internalFonts)
        case LoadFont(path, encoding)    => IO(Figlet4s.loadFont(path, encoding))
        case LoadFontInternal(name)      => IO(Figlet4s.loadFontInternal(name))
        case RenderString(text, options) => IO(Figlet4s.renderString(text, options))
      }
    }

  implicit class Figlet4sActionOps[A](val self: Figlet4sAction[A]) {
    def render: IO[A] = self.foldMap(idCompiler)
  }
}
