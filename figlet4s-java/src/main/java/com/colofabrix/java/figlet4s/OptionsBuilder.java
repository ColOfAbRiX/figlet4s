package com.colofabrix.java.figlet4s;

import com.colofabrix.scala.figlet4s.errors.*;
import com.colofabrix.scala.figlet4s.figfont.FIGfont;
import com.colofabrix.java.figlet4s.options.*;
import com.colofabrix.scala.figlet4s.unsafe.OptionsBuilderMixin;
import scala.io.Codec$;
import scala.io.Codec;

/**
 * Builder of rendering options.
 *
 * This builder works by recording what settings a user wants to use instead of applying the setting immediately when
 * calling a method. This allows for a fail-safe behaviour when, for instance, a user wants to load a file but the file
 * is missing. Instead of receiving an exception when calling OptionsBuilder.withFont the builder will simply record
 * the desire of loading a file. The actual loading, and failure, will happen and handled when calling
 * OptionsBuilder.compile().
 */
public class OptionsBuilder implements OptionsBuilderMixin {

    private final com.colofabrix.scala.figlet4s.options.OptionsBuilder optionsBuilder;
    private final OptionsBuilderOps optionsBuilderOps;

    /**
     * Creates a new OptionsBuilder using a Scala OptionsBuilder object
     *
     * @param optionsBuilder The Scala OptionsBuilder instance that will we wrapped in this object
     */
    public OptionsBuilder(com.colofabrix.scala.figlet4s.options.OptionsBuilder optionsBuilder) {
        this.optionsBuilder = optionsBuilder;
        this.optionsBuilderOps = new OptionsBuilderOps(optionsBuilder);
    }

    //  Text  //

    /**
     * Set the text to render
     *
     * @param text The text to render
     * @return The option builder with a set text to render
     */
    public OptionsBuilder text(String text) {
        return new OptionsBuilder(this.optionsBuilder.text(text));
    }

    //  Font  //

    /**
     * Use the default rendering font
     *
     * @return The option builder with the rendering font set to the default font
     */
    public OptionsBuilder defaultFont() {
        return new OptionsBuilder(this.optionsBuilder.defaultFont());
    }

    /**
     * Use a font loaded from file
     *
     * The loading of the font is performed when the [[RenderOptions]] is built.
     *
     * @param fontName Name of the internal font to load
     * @return The option builder with the rendering font set to the loaded font
     */
    public OptionsBuilder withInternalFont(String fontName) {
        return new OptionsBuilder(this.optionsBuilder.withInternalFont(fontName));
    }

    /**
     * Use a font loaded from file
     *
     * The loading of the font is performed when the [[RenderOptions]] is built.
     *
     * @param fontPath Path of the font, including the extension
     * @return The option builder with the rendering font set to the loaded font
     */
    public OptionsBuilder withFont(String fontPath) {
        return new OptionsBuilder(this.optionsBuilder.withFont(fontPath, Codec$.MODULE$.ISO8859()));
    }

    /**
     * Use a font loaded from file
     *
     * The loading of the font is performed when the [[RenderOptions]] is built.
     *
     * @param fontPath Path of the font, including the extension
     * @param codec Encoding of the font. The default is ISO-8859
     * @return The option builder with the rendering font set to the loaded font
     */
    public OptionsBuilder withFont(String fontPath, Codec codec) {
        return new OptionsBuilder(this.optionsBuilder.withFont(fontPath, codec));
    }

    /**
     * Use a font loaded from file
     *
     * The loading of the font is performed when the [[RenderOptions]] is built.
     *
     * @param fontPath Path of the font, including the extension
     * @param codec Encoding of the font. The default is ISO-8859
     * @return The option builder with the rendering font set to the loaded font
     */
    public OptionsBuilder withFont(String fontPath, String codec) {
        return new OptionsBuilder(this.optionsBuilder.withFont(fontPath, Codec$.MODULE$.apply(codec)));
    }

    /**
     * Use a specific font that's already been loaded
     *
     * @param font The FIGfont to use for rendering
     * @return The option builder with the rendering font set to the specified font
     */
    public OptionsBuilder withFont(FIGfont font) {
        return new OptionsBuilder(this.optionsBuilder.withFont(font));
    }

    //  Horizontal Layout  //

    /**
     * Use the default horizontal layout
     *
     * @return The option builder with the horizontal layout font set to the default one
     */
    public OptionsBuilder defaultHorizontalLayout() {
        return new OptionsBuilder(this.optionsBuilder.defaultHorizontalLayout());
    }

    /**
     * Use the specified horizontal layout to render the text
     *
     * @param layout The horizontal layout to use
     * @return The option builder with the rendering font set to the specified font
     */
    public OptionsBuilder withHorizontalLayout(HorizontalLayout layout) {
        return new OptionsBuilder(
            this.optionsBuilder.withHorizontalLayout(
                HorizontalLayout.toScala(layout)
            )
        );
    }

    //  Max Width  //

    /**
     * Use the default maximum width
     *
     * @return The option builder with the maximum width set to the default one
     */
    public OptionsBuilder defaultMaxWidth() {
        return new OptionsBuilder(this.optionsBuilder.defaultMaxWidth());
    }

    /**
     * Use the specified maximum width to render the text
     *
     * @param maxWidth The maximum width for the rendered text
     * @return The option builder with the maximum width set to the specified one
     */
    public OptionsBuilder withMaxWidth(int maxWidth) {
        return new OptionsBuilder(this.optionsBuilder.withMaxWidth(maxWidth));
    }

    //  Print Direction  //

    /**
     * Use the default print direction
     *
     * @return The option builder with the print direction set to the default one
     */
    public OptionsBuilder defaultPrintDirection() {
        return new OptionsBuilder(this.optionsBuilder.defaultPrintDirection());
    }

    /**
     * Use the specified print direction to render the text
     *
     * @param direction The print direction to use
     * @return The option builder with the print direction set to the specified one
     */
    public OptionsBuilder withPrintDirection(PrintDirection direction) {
        return new OptionsBuilder(
            this.optionsBuilder.withPrintDirection(
                PrintDirection.toScala(direction)
            )
        );
    }

    //  Justification  //

    /**
     * Use the default justification
     *
     * @return The option builder with the justification set to the default one
     */
    public OptionsBuilder defaultJustification() {
        return new OptionsBuilder(this.optionsBuilder.defaultJustification());
    }

    /**
     * Use the specified justification to render the text
     *
     * @param justification The justification to use
     * @return The option builder with the justification set to the specified one
     */
    public OptionsBuilder withJustification(Justification justification) {
        return new OptionsBuilder(
            this.optionsBuilder.withJustification(
                Justification.toScala(justification)
            )
        );
    }

    //  Operations  //

    /**
     * Get the text to render
     *
     * @return The text to render
     */
    public String getText() throws FigletException {
        return this.optionsBuilderOps.text();
    }

    /**
     * Builds the options and then renders the text into a FIGure
     *
     * @return A FIGure representing the rendered text
     */
    public FIGure render() throws FigletException {
        return new FIGure(this.optionsBuilderOps.render());
    }

    /**
     * Builds the options and then renders the text into a FIGure
     *
     * @param text The text to render
     * @return A FIGure representing the rendered text
     */
    public FIGure render(String text) throws FigletException {
        return new FIGure(this.optionsBuilderOps.render(text));
    }

    /**
     * Builds and returns the render options
     *
     * @return The RenderOptions resulting from building the internal state
     */
    public RenderOptions getOptions() throws FigletException {
        return new RenderOptions(this.optionsBuilderOps.options());
    }

}
