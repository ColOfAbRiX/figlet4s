package com.colofabrix.scala.figlet4s.options

import cats.effect.Sync
import cats.implicits._
import com.colofabrix.scala.figlet4s.api.InternalAPI
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options.OptionsBuilder._

/**
 * Builder of rendering options
 */
final class OptionsBuilder(private val builderActions: List[BuilderAction] = List.empty) {

  /** Use the specified Text Width */
  def text(text: String): OptionsBuilder =
    addAction(SetTextAction(text))

  //  Font  //

  /** Use the default FIGfont */
  def defaultFont(): OptionsBuilder =
    addAction(DefaultFontAction)

  /** Use the internal FIGfont with the specified fontName */
  def withInternalFont(fontName: String): OptionsBuilder =
    addAction(LoadInternalFontAction(fontName))

  /** Use the FIGfont with the specified fontPath */
  def withFont(fontPath: String, encoding: String = "ISO8859_1"): OptionsBuilder =
    addAction(LoadFontAction(fontPath, encoding))

  /** Use the specified FIGfont */
  def withFont(font: FIGfont): OptionsBuilder =
    addAction(SetFontAction(font))

  //  Horizontal Layout  //

  /** Use the default Horizontal Layout */
  def defaultHorizontalLayout(): OptionsBuilder =
    addAction(DefaultHorizontalLayout)

  /** Use the specified Horizontal Layout */
  def withHorizontalLayout(layout: HorizontalLayout): OptionsBuilder =
    addAction(SetHorizontalLayout(layout))

  //  Max Width  //

  /** Use the default Max Width */
  def defaultMaxWidth(): OptionsBuilder =
    addAction(DefaultMaxWidthAction)

  /** Use the specified Max Width */
  def withMaxWidth(maxWidth: Int): OptionsBuilder =
    addAction(SetMaxWidthAction(maxWidth))

  //  Print Direction  //

  /** Use the default Max Width */
  def defaultPrintDirection(): OptionsBuilder =
    addAction(DefaultPrintDirection)

  /** Use the specified Max Width */
  def withPrintDirection(direction: PrintDirection): OptionsBuilder =
    addAction(SetPrintDirection(direction))

  //  Support  //

  /** Compiler to execute Actions to obtain BuildData, generic in the effect */
  private[figlet4s] def compile[F[_]: Sync]: F[BuildData] =
    OptionsBuilder.compile(this)

  private def addAction(action: BuilderAction): OptionsBuilder =
    new OptionsBuilder(action :: builderActions)

}

private[figlet4s] object OptionsBuilder {

  sealed trait BuilderAction extends Product with Serializable

  final case object DefaultFontAction       extends BuilderAction
  final case object DefaultHorizontalLayout extends BuilderAction
  final case object DefaultMaxWidthAction   extends BuilderAction
  final case object DefaultPrintDirection   extends BuilderAction

  final case class LoadFontAction(fontPath: String, encoding: String) extends BuilderAction
  final case class LoadInternalFontAction(fontName: String)           extends BuilderAction
  final case class SetFontAction(font: FIGfont)                       extends BuilderAction
  final case class SetHorizontalLayout(layout: HorizontalLayout)      extends BuilderAction
  final case class SetMaxWidthAction(maxWidth: Int)                   extends BuilderAction
  final case class SetPrintDirection(direction: PrintDirection)       extends BuilderAction
  final case class SetTextAction(text: String)                        extends BuilderAction

  final case class BuildData(
      font: Option[FigletResult[FIGfont]] = None,
      horizontalLayout: Option[HorizontalLayout] = None,
      maxWidth: Option[Int] = None,
      printDirection: Option[PrintDirection] = None,
      text: String = "",
  )

  /**
   * Compiler to execute Actions to obtain BuildData, generic in the effect
   */
  def compile[F[_]: Sync](self: OptionsBuilder): F[BuildData] =
    self
      .builderActions
      .foldM(BuildData()) {

        //  Text  //

        case (buildData, SetTextAction(text)) =>
          Sync[F].pure(buildData.copy(text = text))

        //  Max Width  //

        case (buildData, DefaultMaxWidthAction) =>
          Sync[F].pure(buildData.copy(maxWidth = None))

        case (buildData, SetMaxWidthAction(maxWidth)) =>
          Sync[F].pure(buildData.copy(maxWidth = Some(maxWidth)))

        //  Horizontal Layout  //

        case (buildData, DefaultHorizontalLayout) =>
          Sync[F].pure(buildData.copy(horizontalLayout = None))

        case (buildData, SetHorizontalLayout(layout)) =>
          Sync[F].pure(buildData.copy(horizontalLayout = Some(layout)))

        //  Print Direction  //

        case (buildData, DefaultPrintDirection) =>
          Sync[F].pure(buildData.copy(printDirection = None))

        case (buildData, SetPrintDirection(direction)) =>
          Sync[F].pure(buildData.copy(printDirection = Some(direction)))

        //  Fonts  //

        case (buildData, DefaultFontAction) =>
          InternalAPI
            .loadFontInternal[F]("standard")
            .map { font =>
              buildData.copy(font = Some(font))
            }

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
