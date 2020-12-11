package com.colofabrix.java.figlet4s.options;

/**
 * Option to chose the desired horizontal rendering layout
 */
public enum HorizontalLayout {
    /**
     * Full width. Display all FIGcharacters at their full width, which may be fixed or variable, depending on the font
     */
    FULL_WIDTH,

    /**
     * Kerning. As many blanks as possible are removed between FIGcharacters, so that they touch, but the FIGcharacters
     * are not smushed.
     */
    HORIZONTAL_FITTING,

    /**
     * Smushing. The FIGcharacters are displayed as close together as possible, and overlapping sub-characters are
     * removed. Exactly which sub-characters count as overlapping depends on the font's layout mode, which is defined by
     * the font's author. It will not smush a font whose author specified kerning or full width as the default layout mode
     */
    HORIZONTAL_SMUSHING,

    /**
     * Forced smushing. The FIGcharacters are displayed as close together as possible, and overlapping sub-characters are
     * removed. Exactly which sub-characters count as overlapping depends on the font's layout mode, which is defined by
     * the font's author. It will attempt to smush the character even if the font author specified kerning or full width
     * as the default layout mode.
     */
    FORCE_HORIZONTAL_SMUSHING,

    /**
     * Use the default value specified in the FIGfont
     */
    FONT_DEFAULT;

    /**
     * Converts the Java enum HorizontalLayout into the Scala HorizontalLayout ADT
     *
     * @param value The HorizontalLayout instance to convert
     * @return The scala HorizontalLayout instance equivalent to the input value
     */
    public static com.colofabrix.scala.figlet4s.options.HorizontalLayout toScala(HorizontalLayout value) {
        com.colofabrix.scala.figlet4s.options.HorizontalLayout result;

        switch (value) {
            case FULL_WIDTH:
                result = com.colofabrix.scala.figlet4s.options.HorizontalLayout.FullWidth$.MODULE$;
                break;

            case HORIZONTAL_FITTING:
                result = com.colofabrix.scala.figlet4s.options.HorizontalLayout.HorizontalFitting$.MODULE$;
                break;

            case HORIZONTAL_SMUSHING:
                result = com.colofabrix.scala.figlet4s.options.HorizontalLayout.HorizontalSmushing$.MODULE$;
                break;

            case FORCE_HORIZONTAL_SMUSHING:
                result = com.colofabrix.scala.figlet4s.options.HorizontalLayout.ForceHorizontalSmushing$.MODULE$;
                break;

            default:
            case FONT_DEFAULT:
                result = com.colofabrix.scala.figlet4s.options.HorizontalLayout.FontDefault$.MODULE$;
        }

        return result;
    }
}
