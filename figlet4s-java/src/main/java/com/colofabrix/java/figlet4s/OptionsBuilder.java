package com.colofabrix.java.figlet4s;

import com.colofabrix.scala.figlet4s.errors.*;
import com.colofabrix.scala.figlet4s.figfont.FIGfont;
import com.colofabrix.scala.figlet4s.options.HorizontalLayout;
import com.colofabrix.scala.figlet4s.options.Justification;
import com.colofabrix.scala.figlet4s.options.PrintDirection;
import com.colofabrix.scala.figlet4s.options.RenderOptions;
import com.colofabrix.scala.figlet4s.unsafe.OptionsBuilderMixin;
import org.checkerframework.checker.nullness.qual.NonNull;
import scala.io.Codec$;
import scala.io.Codec;

public class OptionsBuilder implements OptionsBuilderMixin {

    private final com.colofabrix.scala.figlet4s.options.OptionsBuilder optionsBuilder;
    private final OptionsBuilderOps optionsBuilderOps;

    public OptionsBuilder(com.colofabrix.scala.figlet4s.options.OptionsBuilder optionsBuilder) {
        this.optionsBuilder = optionsBuilder;
        this.optionsBuilderOps = new OptionsBuilderOps(optionsBuilder);
    }

    //  Text  //

    @NonNull
    public OptionsBuilder text(String text) {
        return new OptionsBuilder(this.optionsBuilder.text(text));
    }

    //  Font  //

    @NonNull
    public OptionsBuilder defaultFont() {
        return new OptionsBuilder(this.optionsBuilder.defaultFont());
    }

    @NonNull
    public OptionsBuilder withInternalFont(String fontName) {
        return new OptionsBuilder(this.optionsBuilder.withInternalFont(fontName));
    }

    @NonNull
    public OptionsBuilder withFont(String fontPath, Codec codec) {
        return new OptionsBuilder(this.optionsBuilder.withFont(fontPath, codec));
    }

    @NonNull
    public OptionsBuilder withFont(String fontPath, String codec) {
        return new OptionsBuilder(this.optionsBuilder.withFont(fontPath, Codec$.MODULE$.apply(codec)));
    }

    @NonNull
    public OptionsBuilder withFont(FIGfont font) {
        return new OptionsBuilder(this.optionsBuilder.withFont(font));
    }

    //  Horizontal Layout  //

    @NonNull
    public OptionsBuilder defaultHorizontalLayout() {
        return new OptionsBuilder(this.optionsBuilder.defaultHorizontalLayout());
    }

    @NonNull
    public OptionsBuilder withHorizontalLayout(HorizontalLayout layout) {
        return new OptionsBuilder(this.optionsBuilder.withHorizontalLayout(layout));
    }

    //  Max Width  //

    @NonNull
    public OptionsBuilder defaultMaxWidth() {
        return new OptionsBuilder(this.optionsBuilder.defaultMaxWidth());
    }

    @NonNull
    public OptionsBuilder withMaxWidth(int maxWidth) {
        return new OptionsBuilder(this.optionsBuilder.withMaxWidth(maxWidth));
    }

    //  Print Direction  //

    @NonNull
    public OptionsBuilder defaultPrintDirection() {
        return new OptionsBuilder(this.optionsBuilder.defaultPrintDirection());
    }

    @NonNull
    public OptionsBuilder withPrintDirection(PrintDirection direction) {
        return new OptionsBuilder(this.optionsBuilder.withPrintDirection(direction));
    }

    //  Justification  //

    @NonNull
    public OptionsBuilder defaultJustification() {
        return new OptionsBuilder(this.optionsBuilder.defaultJustification());
    }

    @NonNull
    public OptionsBuilder withJustification(Justification justification) {
        return new OptionsBuilder(this.optionsBuilder.withJustification(justification));
    }

    //  Operations  //

    @NonNull
    public String getText() throws FigletException {
        return this.optionsBuilderOps.text();
    }

    @NonNull
    public FIGure render() throws FigletException {
        return new FIGure(this.optionsBuilderOps.render());
    }

    @NonNull
    public FIGure render(String text) throws FigletException {
        return new FIGure(this.optionsBuilderOps.render(text));
    }

    @NonNull
    public RenderOptions getOptions() throws FigletException {
        return this.optionsBuilderOps.options();
    }

}
