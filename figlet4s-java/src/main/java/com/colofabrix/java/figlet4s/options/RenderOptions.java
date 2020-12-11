package com.colofabrix.java.figlet4s.options;

import com.colofabrix.scala.figlet4s.figfont.FIGfont;

/**
 * Rendering options, including the FIGfont to use
 */
public class RenderOptions {
    private final FIGfont font;
    private final int maxWidth;
    private final HorizontalLayout horizontalLayout;
    private final PrintDirection printDirection;
    private final Justification justification;

    /**
     * Creates a new RenderOptions
     *
     * @param font             The FIGfont to use to render the text
     * @param horizontalLayout The desired horizontal layout to render the text
     * @param justification    The text justification
     * @param maxWidth         The maximum width of rendered text
     * @param printDirection   The print direction
     */
    public RenderOptions(FIGfont font, int maxWidth, HorizontalLayout horizontalLayout, PrintDirection printDirection, Justification justification) {
        this.font = font;
        this.maxWidth = maxWidth;
        this.horizontalLayout = horizontalLayout;
        this.printDirection = printDirection;
        this.justification = justification;
    }

    /**
     * Converts this object into the Scala original
     *
     * @return Tts this object into the Scala original
     */
    public com.colofabrix.scala.figlet4s.options.RenderOptions toScala() {
        com.colofabrix.scala.figlet4s.options.HorizontalLayout horizontalLayout = HorizontalLayout.toScala(this.horizontalLayout);
        com.colofabrix.scala.figlet4s.options.Justification justification = Justification.toScala(this.justification);
        com.colofabrix.scala.figlet4s.options.PrintDirection printDirection = PrintDirection.toScala(this.printDirection);
        return new com.colofabrix.scala.figlet4s.options.RenderOptions(this.font, this.maxWidth, horizontalLayout, printDirection, justification);
    }

    /**
     * Get the FIGfont to use to render the text
     *
      @return The FIGfont to use to render the text
     */
    public FIGfont getFont() {
        return this.font;
    }

    /**
     * Get the maximum width of rendered text
     *
      @return The maximum width of rendered text
     */
    public int getMaxWidth() {
        return this.maxWidth;
    }

    /**
     * Get the desired horizontal layout to render the text
     *
      @return The desired horizontal layout to render the text
     */
    public HorizontalLayout getHorizontalLayout() {
        return this.horizontalLayout;
    }

    /**
     * Get the print direction
     *
      @return The print direction
     */
    public PrintDirection getPrintDirection() {
        return this.printDirection;
    }

    /**
     * Get the text justification
     *
      @return The text justification
     */
    public Justification getJustification() {
        return this.justification;
    }
}
