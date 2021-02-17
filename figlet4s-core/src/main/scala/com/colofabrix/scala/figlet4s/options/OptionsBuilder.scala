package com.colofabrix.scala.figlet4s.options

import cats.Applicative
import cats.effect.Sync
import cats.implicits._
import com.colofabrix.scala.figlet4s.core._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options.BuilderAction._
import com.colofabrix.scala.figlet4s.options.OptionsBuilder._
import scala.io.Codec

/**
 * Builder of rendering options.
 *
 * This builder works by recording what settings a user wants to use instead of applying the setting immediately when
 * calling a method. This allows for a fail-safe behaviour when, for instance, a user wants to load a file but the file
 * is missing. Instead of receiving an exception when calling OptionsBuilder.withFont the builder will simply record
 * the desire of loading a file. The actual loading, and failure, will happen and handled when calling
 * OptionsBuilder.compile().
 *
 * To create a builder use the [[com.colofabrix.scala.figlet4s.api.Figlet4sAPI.builder()* Figlet4sAPI.builder()]] or
 * [[com.colofabrix.scala.figlet4s.api.Figlet4sEffectfulAPI.builderF()* Figlet4sEffectfulAPI.builderF()]].
 *
 * @param actions The list of building actions that will be executed to obtain the final configuration.
 */
final class OptionsBuilder(private[figlet4s] val actions: List[BuilderAction] = List.empty) {

  /**
   * Set the text to render
   *
   * @param text The text to render
   * @return The option builder with a set text to render
   */
  def text(text: String): OptionsBuilder =
    addAction(SetTextAction(text))

  //  Font  //

  /**
   * Use the default rendering font
   *
   * @return The option builder with the rendering font set to the default font
   */
  def defaultFont(): OptionsBuilder =
    addAction(DefaultFontAction)

  /**
   * Use one of the internal fonts.
   *
   * The loading of the font is performed when the [[RenderOptions]] is built.
   *
   * @param fontName Name of the internal font to load
   * @return The option builder with the rendering font set to the loaded font
   */
  def withInternalFont(fontName: String): OptionsBuilder =
    addAction(LoadInternalFontAction(fontName))

  /**
   * Use a font loaded from file
   *
   * The loading of the font is performed when the [[RenderOptions]] is built.
   *
   * @param fontPath Path of the font, including the extension
   * @param codec Encoding of the font. The default is ISO-8859
   * @return The option builder with the rendering font set to the loaded font
   */
  def withFont(fontPath: String, codec: Codec = Codec.ISO8859): OptionsBuilder =
    addAction(LoadFontAction(fontPath, codec))

  /**
   * Use a specific font that's already been loaded
   *
   * @param font The FIGfont to use for rendering
   * @return The option builder with the rendering font set to the specified font
   */
  def withFont(font: FIGfont): OptionsBuilder =
    addAction(SetFontAction(font))

  //  Horizontal Layout  //

  /**
   * Use the default horizontal layout
   *
   * @return The option builder with the horizontal layout font set to the default one
   */
  def defaultHorizontalLayout(): OptionsBuilder =
    addAction(DefaultHorizontalLayout)

  /**
   * Use the specified horizontal layout to render the text
   *
   * @param layout The horizontal layout to use
   * @return The option builder with the rendering font set to the specified font
   */
  def withHorizontalLayout(layout: HorizontalLayout): OptionsBuilder =
    addAction(SetHorizontalLayout(layout))

  //  Max Width  //

  /**
   * Use the default maximum width
   *
   * @return The option builder with the maximum width set to the default one
   */
  def defaultMaxWidth(): OptionsBuilder =
    addAction(DefaultMaxWidthAction)

  /**
   * Use the specified maximum width to render the text
   *
   * @param maxWidth The maximum width for the rendered text
   * @return The option builder with the maximum width set to the specified one
   */
  def withMaxWidth(maxWidth: Int): OptionsBuilder =
    addAction(SetMaxWidthAction(maxWidth))

  //  Print Direction  //

  /**
   * Use the default print direction
   *
   * @todo This feature is not yet implemented
   * @return The option builder with the print direction set to the default one
   */
  def defaultPrintDirection(): OptionsBuilder =
    addAction(DefaultPrintDirection)

  /**
   * Use the specified print direction to render the text
   *
   * @todo This feature is not yet implemented
   * @param direction The print direction to use
   * @return The option builder with the print direction set to the specified one
   */
  def withPrintDirection(direction: PrintDirection): OptionsBuilder =
    addAction(SetPrintDirection(direction))

  //  Justification  //

  /**
   * Use the default justification
   *
   * @todo This feature is not yet implemented
   * @return The option builder with the justification set to the default one
   */
  def defaultJustification(): OptionsBuilder =
    addAction(DefaultJustification)

  /**
   * Use the specified justification to render the text
   *
   * @todo This feature is not yet implemented
   * @param justification The justification to use
   * @return The option builder with the justification set to the specified one
   */
  def withJustification(justification: Justification): OptionsBuilder =
    addAction(SetJustification(justification))

  //  Support  //

  private[figlet4s] def compile[F[_]: Sync]: F[BuildData] =
    OptionsBuilder.compile[F](this)

  private def addAction(action: BuilderAction): OptionsBuilder =
    new OptionsBuilder(action :: this.actions.filter(!BuilderAction.sameGroupAs(action)(_)))

}

private[figlet4s] object OptionsBuilder {

  final case class BuildData(
      font: Option[FigletResult[FIGfont]] = None,
      horizontalLayout: HorizontalLayout = HorizontalLayout.FontDefault,
      justification: Justification = Justification.FontDefault,
      maxWidth: Option[Int] = None,
      printDirection: PrintDirection = PrintDirection.FontDefault,
      text: String = "",
  )

  private type ActionCompiler[F[_]] = PartialFunction[(BuildData, BuilderAction), F[BuildData]]

  /**
   * Creates a new OptionsBuilder with default settings
   *
   * @return A new OptionsBuilder
   */
  def apply(): OptionsBuilder =
    new OptionsBuilder()

  /**
   * Creates a new OptionsBuilder with default settings and using an initial actions
   *
   * @param initialAction The first action to be stored in the builder
   * @return A new OptionsBuilder
   */
  def apply(initialAction: BuilderAction): OptionsBuilder =
    new OptionsBuilder(initialAction :: Nil)

  /**
   * Compiler to run BuilderAction that create BuildData, generic in the effect
   */
  def compile[F[_]: Sync](self: OptionsBuilder): F[BuildData] =
    self
      .actions
      .foldM(BuildData())(foldCompilers(allCompilers))

  private def allCompilers[F[_]: Sync]: List[ActionCompiler[F]] =
    List(
      compileFonts[F],
      compileHorizontalLayout[F],
      compileJustification[F],
      compileMaxWidth[F],
      compilePrintDirection[F],
      compileText[F],
    )

  /** Compiles the settings for Text */
  private def compileText[F[_]: Applicative]: ActionCompiler[F] = {
    case (buildData, SetTextAction(text)) =>
      Applicative[F].pure(buildData.copy(text = text))
  }

  /** Compiles the settings for Fonts */
  private def compileFonts[F[_]: Sync]: ActionCompiler[F] = {
    case (buildData, DefaultFontAction) =>
      Figlet4sClient
        .loadFontInternal[F](Figlet4sClient.defaultFont)
        .map { font =>
          buildData.copy(font = Some(font))
        }

    case (buildData, SetFontAction(font)) =>
      Sync[F].pure(buildData.copy(font = Some(font.validNec)))

    case (buildData, LoadFontAction(fontPath, encoding)) =>
      Figlet4sClient
        .loadFont[F](fontPath, encoding)
        .map { font =>
          buildData.copy(font = Some(font))
        }

    case (buildData, LoadInternalFontAction(fontName)) =>
      Figlet4sClient
        .loadFontInternal[F](fontName)
        .map { font =>
          buildData.copy(font = Some(font))
        }
  }

  /** Compiles the settings for Max Width */
  private def compileMaxWidth[F[_]: Applicative]: ActionCompiler[F] = {
    case (buildData, DefaultMaxWidthAction) =>
      Applicative[F].pure(buildData.copy(maxWidth = None))

    case (buildData, SetMaxWidthAction(maxWidth)) =>
      Applicative[F].pure(buildData.copy(maxWidth = Some(maxWidth)))
  }

  /** Compiles the settings for Horizontal Layout */
  private def compileHorizontalLayout[F[_]: Applicative]: ActionCompiler[F] = {
    case (buildData, DefaultHorizontalLayout) =>
      Applicative[F].pure(buildData.copy(horizontalLayout = HorizontalLayout.FontDefault))

    case (buildData, SetHorizontalLayout(layout)) =>
      Applicative[F].pure(buildData.copy(horizontalLayout = layout))
  }

  /** Compiles the settings for Print Direction */
  private def compilePrintDirection[F[_]: Applicative]: ActionCompiler[F] = {
    case (buildData, DefaultPrintDirection) =>
      Applicative[F].pure(buildData.copy(printDirection = PrintDirection.FontDefault))

    case (buildData, SetPrintDirection(direction)) =>
      Applicative[F].pure(buildData.copy(printDirection = direction))
  }

  /** Compiles the settings for Justification */
  private def compileJustification[F[_]: Applicative]: ActionCompiler[F] = {
    case (buildData, DefaultJustification) =>
      Applicative[F].pure(buildData.copy(printDirection = PrintDirection.FontDefault))

    case (buildData, SetJustification(justification)) =>
      Applicative[F].pure(buildData.copy(justification = justification))
  }

  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  private def foldCompilers[F[_]: Sync](data: List[ActionCompiler[F]]): (BuildData, BuilderAction) => F[BuildData] =
    Function.untupled(data.reduce(_ orElse _))

}
