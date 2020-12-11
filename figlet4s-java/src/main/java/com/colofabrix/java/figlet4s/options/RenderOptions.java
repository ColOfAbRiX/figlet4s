package com.colofabrix.java.figlet4s.options;

import com.colofabrix.scala.figlet4s.figfont.FIGfont;

public class RenderOptions {
    private final FIGfont font;
    private final int maxWidth;
    private final HorizontalLayout horizontalLayout;
    private final PrintDirection printDirection;
    private final Justification justification;

    public RenderOptions(FIGfont font, int maxWidth, HorizontalLayout horizontalLayout, PrintDirection printDirection, Justification justification) {
        this.font = font;
        this.maxWidth = maxWidth;
        this.horizontalLayout = horizontalLayout;
        this.printDirection = printDirection;
        this.justification = justification;
    }

    public com.colofabrix.scala.figlet4s.options.RenderOptions toScala() {
        com.colofabrix.scala.figlet4s.options.HorizontalLayout horizontalLayout = HorizontalLayout.toScala(this.horizontalLayout);
        com.colofabrix.scala.figlet4s.options.Justification justification = Justification.toScala(this.justification);
        com.colofabrix.scala.figlet4s.options.PrintDirection printDirection = PrintDirection.toScala(this.printDirection);
        return new com.colofabrix.scala.figlet4s.options.RenderOptions(this.font, this.maxWidth, horizontalLayout, printDirection, justification);
    }

    FIGfont getFont() {
        return this.font;
    }

    int getMaxWidth() {
        return this.maxWidth;
    }

    HorizontalLayout getHorizontalLayout() {
        return this.horizontalLayout;
    }

    PrintDirection getPrintDirection() {
        return this.printDirection;
    }

    Justification getJustification() {
        return this.justification;
    }
}
