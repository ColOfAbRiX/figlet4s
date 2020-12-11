package com.colofabrix.java.figlet4s.options;

public enum PrintDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    FONT_DEFAULT;

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
