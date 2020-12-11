package com.colofabrix.java.figlet4s.options;

public enum Justification {
    CENTER,
    FLUSH_RIGHT,
    FLUSH_LEFT,
    FONT_DEFAULT;

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
