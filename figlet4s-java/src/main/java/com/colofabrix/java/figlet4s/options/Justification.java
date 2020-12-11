package com.colofabrix.java.figlet4s.options;

/**
 * Option to choose the justification of the text
 */
public enum Justification {
    /** Centers the output horizontally */
    CENTER,
    /** Makes it flush-right */
    FLUSH_RIGHT,
    /** Makes the output flush-left */
    FLUSH_LEFT,
    /** Use the default value specified in the FIGfont */
    FONT_DEFAULT;

    /**
     * Converts the Java enum Justification into the Scala Justification ADT
     *
     * @param value The Justification instance to convert
     * @return The scala Justification instance equivalent to the input value
     */
    public static com.colofabrix.scala.figlet4s.options.Justification toScala(Justification value) {
        com.colofabrix.scala.figlet4s.options.Justification result;

        switch (value) {
            case CENTER:
                result = com.colofabrix.scala.figlet4s.options.Justification.Center$.MODULE$;
                break;

            case FLUSH_LEFT:
                result = com.colofabrix.scala.figlet4s.options.Justification.FlushLeft$.MODULE$;
                break;

            case FLUSH_RIGHT:
                result = com.colofabrix.scala.figlet4s.options.Justification.FlushRight$.MODULE$;
                break;

            default:
            case FONT_DEFAULT:
                result = com.colofabrix.scala.figlet4s.options.Justification.FontDefault$.MODULE$;
        }

        return result;
    }
}
