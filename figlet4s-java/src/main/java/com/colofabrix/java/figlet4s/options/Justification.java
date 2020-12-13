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
     * @param value The Java Justification instance to convert
     * @return The Scala Justification instance equivalent to the input value
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

    /**
     * Converts the Scala Justification ADT into the Java enum Justification
     *
     * @param value The Scala Justification instance to convert
     * @return The Java Justification instance equivalent to the input value
     */
    public static Justification fromScala(com.colofabrix.scala.figlet4s.options.Justification value) {
        Justification result;

        if (value instanceof com.colofabrix.scala.figlet4s.options.Justification.Center$) {
            result = CENTER;
        }
        else if (value instanceof com.colofabrix.scala.figlet4s.options.Justification.FlushLeft$) {
            result = FLUSH_LEFT;
        }
        else if (value instanceof com.colofabrix.scala.figlet4s.options.Justification.FlushRight$) {
            result = FLUSH_RIGHT;
        }
        else {
            result = FONT_DEFAULT;
        }

        return result;
    }
}
