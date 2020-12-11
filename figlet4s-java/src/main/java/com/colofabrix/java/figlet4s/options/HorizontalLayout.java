package com.colofabrix.java.figlet4s.options;

public enum HorizontalLayout {
    FULL_WIDTH,
    HORIZONTAL_FITTING,
    HORIZONTAL_SMUSHING,
    FORCE_HORIZONTAL_SMUSHING,
    FONT_DEFAULT;

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
