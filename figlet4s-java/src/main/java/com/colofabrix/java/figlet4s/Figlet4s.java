package com.colofabrix.java.figlet4s;

import com.colofabrix.scala.figlet4s.errors.*;
import com.colofabrix.scala.figlet4s.figfont.FIGfont;
import com.colofabrix.scala.figlet4s.options.*;
import java.util.List;
import scala.collection.Seq;
import scala.io.Codec$;
import scala.io.Codec;
import scala.jdk.CollectionConverters;

public class Figlet4s {

    public List<String> internalFonts() throws FigletException {
        Seq<String> result = com.colofabrix.scala.figlet4s.unsafe.Figlet4s.internalFonts();
        return CollectionConverters.SeqHasAsJava(result).asJava();
    }

    public FIGfont loadFontInternal() throws FigletException {
        return this.loadFontInternal("standard");
    }

    public FIGfont loadFontInternal(String name) throws FigletException {
        return com.colofabrix.scala.figlet4s.unsafe.Figlet4s.loadFontInternal(name);
    }

    public FIGfont loadFont(String path) throws FigletException {
        return this.loadFont(path, Codec$.MODULE$.ISO8859());
    }

    public FIGfont loadFont(String path, String codec) throws FigletException {
        return this.loadFont(path, Codec$.MODULE$.apply(codec));
    }

    public FIGfont loadFont(String path, Codec codec) throws FigletException {
        return com.colofabrix.scala.figlet4s.unsafe.Figlet4s.loadFont(path, codec);
    }

    public FIGure renderString(String text, RenderOptions options) {
        return new FIGure(com.colofabrix.scala.figlet4s.unsafe.Figlet4s.renderString(text, options));
    }

    public OptionsBuilder builder() {
        return com.colofabrix.scala.figlet4s.unsafe.Figlet4s.builder();
    }

    public OptionsBuilder builder(String text) {
        return com.colofabrix.scala.figlet4s.unsafe.Figlet4s.builder(text);
    }

}
