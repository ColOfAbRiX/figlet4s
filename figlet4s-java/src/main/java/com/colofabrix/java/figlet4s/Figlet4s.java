package com.colofabrix.java.figlet4s;

import com.colofabrix.java.figlet4s.options.RenderOptions;
import com.colofabrix.scala.figlet4s.errors.*;
import com.colofabrix.scala.figlet4s.figfont.FIGfont;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import scala.collection.Seq;
import scala.io.Codec$;
import scala.io.Codec;
import scala.jdk.CollectionConverters;

public class Figlet4s {

    @NonNull
    public static List<String> internalFonts() throws FigletException {
        Seq<String> result = com.colofabrix.scala.figlet4s.unsafe.Figlet4s.internalFonts();
        return CollectionConverters.SeqHasAsJava(result).asJava();
    }

    @NonNull
    public static FIGfont loadFontInternal() throws FigletException {
        return loadFontInternal("standard");
    }

    @NonNull
    public static FIGfont loadFontInternal(String name) throws FigletException {
        ScalaInterop.valueNotNull("name", name);
        return com.colofabrix.scala.figlet4s.unsafe.Figlet4s.loadFontInternal(name);
    }

    @NonNull
    public static FIGfont loadFont(String path) throws FigletException {
        ScalaInterop.valueNotNull("path", path);
        return loadFont(path, Codec$.MODULE$.ISO8859());
    }

    @NonNull
    public static FIGfont loadFont(String path, String codec) throws FigletException {
        ScalaInterop.valueNotNull("path", path);
        ScalaInterop.valueNotNull("codec", codec);
        return loadFont(path, Codec$.MODULE$.apply(codec));
    }

    @NonNull
    public static FIGfont loadFont(String path, Codec codec) throws FigletException {
        ScalaInterop.valueNotNull("path", path);
        ScalaInterop.valueNotNull("codec", codec);
        return com.colofabrix.scala.figlet4s.unsafe.Figlet4s.loadFont(path, codec);
    }

    @NonNull
    public static FIGure renderString(String text, RenderOptions options) {
        ScalaInterop.valueNotNull("text", text);
        ScalaInterop.valueNotNull("options", options);
        com.colofabrix.scala.figlet4s.options.RenderOptions scalaOptions = options.toScala();
        return new FIGure(com.colofabrix.scala.figlet4s.unsafe.Figlet4s.renderString(text, scalaOptions));
    }

    @NonNull
    public static OptionsBuilder builder() {
        return new OptionsBuilder(com.colofabrix.scala.figlet4s.unsafe.Figlet4s.builder());
    }

    @NonNull
    public static OptionsBuilder builder(String text) {
        ScalaInterop.valueNotNull("text", text);
        return new OptionsBuilder(com.colofabrix.scala.figlet4s.unsafe.Figlet4s.builder(text));
    }

}
