package com.colofabrix.java.figlet4s;

import com.colofabrix.scala.figlet4s.figfont.FIGfont;
import com.colofabrix.java.figlet4s.options.*;

public class Main {

    public void main(String[] args) {
        Figlet4s
            .builder("Hello, World!")
            .render()
            .print();

        FIGfont font                  = Figlet4s.loadFontInternal("standard");
        int maxWidth                  = 120;
        HorizontalLayout hLayout      = HorizontalLayout.HORIZONTAL_FITTING;
        PrintDirection printDirection = PrintDirection.LEFT_TO_RIGHT;
        Justification justification   = Justification.FONT_DEFAULT;

        RenderOptions options = new RenderOptions(font,maxWidth, hLayout, printDirection, justification);

        FIGure figure = Figlet4s.renderString("Hello, World!", options);

        figure.print();
    }

}
