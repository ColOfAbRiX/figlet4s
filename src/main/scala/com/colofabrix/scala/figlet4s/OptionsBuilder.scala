package com.colofabrix.scala.figlet4s

import cats.effect.Sync
import cats.implicits._
import com.colofabrix.scala.figlet4s.OptionsBuilder._
import com.colofabrix.scala.figlet4s.api.InternalAPI
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.figfont._

/**
 * Builder of rendering options
 */
final class OptionsBuilder(private val builderActions: List[BuilderActions] = List.empty) {

  /** Use the specified Text Width */
  def text(text: String): OptionsBuilder =
    new OptionsBuilder(SetTextAction(text) :: builderActions)

  //  Font  //

  /** Use the default FIGfont */
  def defaultFont(): OptionsBuilder =
    new OptionsBuilder(DefaultFontAction :: builderActions)

  /** Use the internal FIGfont with the specified fontName */
  def withInternalFont(fontName: String): OptionsBuilder =
    new OptionsBuilder(LoadInternalFontAction(fontName) :: builderActions)

  /** Use the FIGfont with the specified fontPath */
  def withFont(fontPath: String, encoding: String = "ISO8859_1"): OptionsBuilder =
    new OptionsBuilder(LoadFontAction(fontPath, encoding) :: builderActions)

  /** Use the specified FIGfont */
  def withFont(font: FIGfont): OptionsBuilder =
    new OptionsBuilder(SetFontAction(font) :: builderActions)

  //  Horizontal Layout  //

  /** Use the default Horizontal Layout */
  def defaultHorizontalLayout(): OptionsBuilder =
    new OptionsBuilder(DefaultHorizontalLayout :: builderActions)

  /** Use the specified Horizontal Layout */
  def withHorizontalLayout(layout: HorizontalLayout): OptionsBuilder =
    new OptionsBuilder(SetHorizontalLayout(layout) :: builderActions)

  //  Max Width  //

  /** Use the default Max Width */
  def defaultMaxWidth(): OptionsBuilder =
    new OptionsBuilder(DefaultMaxWidthAction :: builderActions)

  /** Use the specified Max Width */
  def withMaxWidth(maxWidth: Int): OptionsBuilder =
    new OptionsBuilder(SetMaxWidthAction(maxWidth) :: builderActions)

  /** Compiler to execute Actions to obtain BuildData, generic in the effect */
  private[figlet4s] def compile[F[_]: Sync]: F[BuildData] =
    OptionsBuilder.compile(this)

}

private[figlet4s] object OptionsBuilder {

  sealed trait BuilderActions extends Product with Serializable

  final case object DefaultFontAction       extends BuilderActions
  final case object DefaultHorizontalLayout extends BuilderActions
  final case object DefaultMaxWidthAction   extends BuilderActions

  final case class SetTextAction(text: String)                        extends BuilderActions
  final case class SetMaxWidthAction(maxWidth: Int)                   extends BuilderActions
  final case class SetHorizontalLayout(layout: HorizontalLayout)      extends BuilderActions
  final case class SetFontAction(font: FIGfont)                       extends BuilderActions
  final case class LoadFontAction(fontPath: String, encoding: String) extends BuilderActions
  final case class LoadInternalFontAction(fontName: String)           extends BuilderActions

  final case class BuildData(
      text: String = "",
      font: Option[FigletResult[FIGfont]] = None,
      horizontalLayout: Option[HorizontalLayout] = None,
      maxWidth: Option[Int] = None,
  )

  /**
   * Compiler to execute Actions to obtain BuildData, generic in the effect
   */
  def compile[F[_]: Sync](self: OptionsBuilder): F[BuildData] =
    self
      .builderActions
      .foldM(BuildData()) {
        case (buildData, DefaultFontAction) =>
          InternalAPI
            .loadFontInternal[F]("standard")
            .map { font =>
              buildData.copy(font = Some(font))
            }

        case (buildData, DefaultHorizontalLayout) =>
          Sync[F].pure(buildData.copy(horizontalLayout = None))

        case (buildData, DefaultMaxWidthAction) =>
          Sync[F].pure(buildData.copy(maxWidth = None))

        //  Text  //

        case (buildData, SetTextAction(text)) =>
          Sync[F].pure(buildData.copy(text = text))

        //  Max Width  //

        case (buildData, SetMaxWidthAction(maxWidth)) =>
          Sync[F].pure(buildData.copy(maxWidth = Some(maxWidth)))

        //  Horizontal Layout  //

        case (buildData, SetHorizontalLayout(layout)) =>
          Sync[F].pure(buildData.copy(horizontalLayout = Some(layout)))

        //  Fonts  //

        case (buildData, SetFontAction(font)) =>
          Sync[F].pure(buildData.copy(font = Some(font.validNec)))

        case (buildData, LoadFontAction(fontPath, encoding)) =>
          InternalAPI
            .loadFont[F](fontPath, encoding)
            .map { font =>
              buildData.copy(font = Some(font))
            }

        case (buildData, LoadInternalFontAction(fontName)) =>
          InternalAPI
            .loadFontInternal(fontName)
            .map { font =>
              buildData.copy(font = Some(font))
            }
      }
}
