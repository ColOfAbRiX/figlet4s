package com.colofabrix.java.figlet4s.options;

/**
 * Option to choose the rendering direction
 */
public enum PrintDirection {
    /** Render the text left-to-right */
    LEFT_TO_RIGHT,
    /** Render the text right-to-left */
    RIGHT_TO_LEFT,
    /** Use the default value specified in the FIGfont */
    FONT_DEFAULT;

    /**
     * Converts the Java enum PrintDirection into the Scala PrintDirection ADT
     *
     * @param value The PrintDirection instance to convert
     * @return The scala PrintDirection instance equivalent to the input value
     */
    public static com.colofabrix.scala.figlet4s.options.PrintDirection toScala(PrintDirection value) {
        com.colofabrix.scala.figlet4s.options.PrintDirection result;

        switch (value) {
            case LEFT_TO_RIGHT:
                result = com.colofabrix.scala.figlet4s.options.PrintDirection.LeftToRight$.MODULE$;
                break;

            case RIGHT_TO_LEFT:
                result = com.colofabrix.scala.figlet4s.options.PrintDirection.RightToLeft$.MODULE$;
                break;

            default:
            case FONT_DEFAULT:
                result = com.colofabrix.scala.figlet4s.options.PrintDirection.FontDefault$.MODULE$;
        }

        return result;
    }
}
