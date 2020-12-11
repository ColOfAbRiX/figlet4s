package com.colofabrix.java.figlet4s;

import com.colofabrix.java.figlet4s.options.RenderOptions;
import com.colofabrix.scala.figlet4s.errors.*;
import com.colofabrix.scala.figlet4s.figfont.FIGfont;
import java.util.List;
import scala.collection.Seq;
import scala.io.Codec$;
import scala.io.Codec;
import scala.jdk.CollectionConverters;

/**
 * "FIGlet" stands for "Frank, Ian and Glenn's LETters and this is a pure Scala implementation.
 * <p>
 * This Figlet client returns only pure values and might throw exceptions
 */
public class Figlet4s {

    /**
     * The list of available internal fonts
     *
     * @return The collection of names of FIGfonts shipped with this library
     */
    public static List<String> internalFonts() throws FigletException {
        Seq<String> result = com.colofabrix.scala.figlet4s.unsafe.Figlet4s.internalFonts();
        return CollectionConverters.SeqHasAsJava(result).asJava();
    }

    /**
     * Loads the FIGfont "standard"
     *
     * @return The "standard" FIGfont
     */
    public static FIGfont loadFontInternal() throws FigletException {
        return loadFontInternal("standard");
    }

    /**
     * Loads one of the internal FIGfont
     *
     * @param name The name of the internal font to load, defaults to "standard"
     * @return The FIGfont of the requested internal font
     */
    public static FIGfont loadFontInternal(String name) throws FigletException {
        return com.colofabrix.scala.figlet4s.unsafe.Figlet4s.loadFontInternal(name);
    }

    /**
     * Loads a FIGfont from file
     *
     * @param path The path of the font file to load. It can be a .flf file or a zipped file.
     * @return The FIGfont loaded from the specified path
     */
    public static FIGfont loadFont(String path) throws FigletException {
        return loadFont(path, Codec$.MODULE$.ISO8859());
    }

    /**
     * Loads a FIGfont from file
     *
     * @param path  The path of the font file to load. It can be a .flf file or a zipped file.
     * @param codec The codec of the file if textual. If it is a zipped file it will be ignored
     * @return The FIGfont loaded from the specified path
     */
    public static FIGfont loadFont(String path, String codec) throws FigletException {
        return loadFont(path, Codec$.MODULE$.apply(codec));
    }

    /**
     * Loads a FIGfont from file
     *
     * @param path  The path of the font file to load. It can be a .flf file or a zipped file.
     * @param codec The codec of the file if textual. If it is a zipped file it will be ignored
     * @return The FIGfont loaded from the specified path
     */
    public static FIGfont loadFont(String path, Codec codec) throws FigletException {
        return com.colofabrix.scala.figlet4s.unsafe.Figlet4s.loadFont(path, codec);
    }

    /**
     * Renders a given text as a FIGure
     *
     * @param text    The text to render
     * @param options The rendering options used to render the text
     * @return A FIGure representing the rendered text
     */
    public static FIGure renderString(String text, RenderOptions options) {
        com.colofabrix.scala.figlet4s.options.RenderOptions scalaOptions = options.toScala();
        return new FIGure(com.colofabrix.scala.figlet4s.unsafe.Figlet4s.renderString(text, scalaOptions));
    }

    //  Builder  //

    /**
     * Returns a new options builder with default settings
     *
     * @return An OptionBuilder to build the rendering options
     */
    public static OptionsBuilder builder() {
        return new OptionsBuilder(com.colofabrix.scala.figlet4s.unsafe.Figlet4s.builder());
    }

    /**
     * Returns a new options builder with default settings and containing the specified text to render
     *
     * @param text The text to render
     * @return An OptionBuilder to build the rendering options
     */
    public static OptionsBuilder builder(String text) {
        return new OptionsBuilder(com.colofabrix.scala.figlet4s.unsafe.Figlet4s.builder(text));
    }

}
