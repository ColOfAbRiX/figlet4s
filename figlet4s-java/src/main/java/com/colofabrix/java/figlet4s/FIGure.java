package com.colofabrix.java.figlet4s;

import com.colofabrix.scala.figlet4s.figfont.FIGfont;
import com.colofabrix.scala.figlet4s.figfont.SubColumns;
import com.colofabrix.scala.figlet4s.figfont.SubLines;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import scala.jdk.CollectionConverters;

/**
 * A FIGure that is a rendered String with a specific FIGfont and built from multiple FIGcharacters
 */
public class FIGure {

    private final com.colofabrix.scala.figlet4s.figfont.FIGure figure;

    /**
     * Creates a new FIGure using a Scala FIGure object
     *
     * @param figure The Scala FIGure instance that will we wrapped in this object
     */
    public FIGure(com.colofabrix.scala.figlet4s.figfont.FIGure figure) {
        this.figure = figure;
    }

    /**
     * Get the FIGfont used to render the FIGure
     *
     * @return The FIGfont instance used to render the FIGure
     */
    public FIGfont getFont() {
        return this.figure.font();
    }

    /**
     * Get the String value rendered in the FIGure
     *
     * @return The String value rendered in the FIGure
     */
    public String getValue() {
        return this.figure.value();
    }

    /**
     * Get the FIGure represented with a collection of columns
     *
     * @return A list of columns where each represents a column of the rendered text
     */
    public List<List<String>> getColumns() {
        return ScalaInterop.nestedSeqAsList(this.figure.columns(), SubColumns::value);
    }

    /**
     * Get the columns representing the rendered FIGure stripped of their hardblanks
     *
     * @return A list of columns where each represents a column of the rendered text
     */
    public List<List<String>> getCleanColumns() {
        return ScalaInterop.nestedSeqAsList(this.figure.cleanColumns(), SubColumns::value);
    }

    /**
     * Get the FIGure represented with a collection of lines
     *
     * @return A list of columns where each represents a line of the rendered text
     */
    public List<List<String>> getLines() {
        return ScalaInterop.nestedSeqAsList(this.figure.lines(), SubLines::value);
    }

    /**
     * Get the FIGure represented with a list of lines stripped of their hardblanks
     *
     * @return A list of columns where each represents a line of the rendered text
     */
    public List<List<String>> getCleanLines() {
        return ScalaInterop.nestedSeqAsList(this.figure.cleanLines(), SubLines::value);
    }

    /**
     * The max width of the FIGure
     *
     * @return The maximum width of the FIGure
     */
    public int getWidth() {
        return this.figure.width();
    }

    /**
     * Apply a function to each displayable line of the FIGure
     *
     * @param f The function to apply to each displayable line
     */
    public void foreachLine(Consumer<? super String> f) {
        this.getCleanLines().forEach(lines -> lines.forEach(f));
    }

    /**
     * Print the FIGure to standard output
     */
    public void print() {
        this.asList().forEach(scala.Console::println);
    }

    /**
     * The figure as a collection of String, one String per displayable line
     *
     * @return A collection of strings, each containing a displayable line
     */
    public List<String> asList() {
        return this.getCleanLines().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * The figure as single String and newline characters
     *
     * @return A single string containing the FIGure including newlines where needed
     */
    public String asString() {
        return this.asList().stream().collect(Collectors.joining(System.lineSeparator()));
    }
}
