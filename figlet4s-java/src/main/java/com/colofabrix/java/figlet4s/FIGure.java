package com.colofabrix.java.figlet4s;

import com.colofabrix.scala.figlet4s.figfont.FIGfont;
import com.colofabrix.scala.figlet4s.figfont.SubColumns;
import com.colofabrix.scala.figlet4s.figfont.SubLines;
import com.colofabrix.scala.figlet4s.unsafe.FIGureMixin;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import scala.collection.immutable.Seq;
import scala.jdk.CollectionConverters;

public class FIGure implements FIGureMixin {

    private final com.colofabrix.scala.figlet4s.figfont.FIGure figure;
    private final FIGureOps fiGureOps;

    public FIGure(com.colofabrix.scala.figlet4s.figfont.FIGure figure) {
        ScalaInterop.valueNotNull("figure", figure);

        this.figure = figure;
        this.fiGureOps = new FIGureOps(figure);
    }

    public FIGure(FIGfont font, String value, List<List<String>> subcolumns) {
        ScalaInterop.valueNotNull("font", font);
        ScalaInterop.valueNotNull("value", value);
        ScalaInterop.listNotNull("subcolumns", subcolumns);

        List<SubColumns> javaSubcolumns = subcolumns
            .stream()
            .map(e -> new SubColumns(CollectionConverters.ListHasAsScala(e).asScala().toVector()))
            .collect(Collectors.toList());

        Seq<SubColumns> scalaSubcolumns = CollectionConverters
            .ListHasAsScala(javaSubcolumns)
            .asScala()
            .toVector();

        this.figure = new com.colofabrix.scala.figlet4s.figfont.FIGure(font, value, scalaSubcolumns);
        this.fiGureOps = new FIGureOps(this.figure);
    }

    @NonNull
    public FIGfont getFont() {
        return this.figure.font();
    }

    @NonNull
    public String getValue() {
        return this.figure.value();
    }

    @NonNull
    public List<List<String>> getColumns() {
        return ScalaInterop.nestedSeqAsList(this.figure.columns(), SubColumns::value);
    }

    @NonNull
    public List<List<String>> getCleanColumns() {
        return ScalaInterop.nestedSeqAsList(this.figure.cleanColumns(), SubColumns::value);
    }

    @NonNull
    public List<List<String>> getLines() {
        return ScalaInterop.nestedSeqAsList(this.figure.lines(), SubLines::value);
    }

    @NonNull
    public List<List<String>> getCleanLines() {
        return ScalaInterop.nestedSeqAsList(this.figure.cleanLines(), SubLines::value);
    }

    public int getWidth() {
        return this.figure.width();
    }

    public void foreachLine(Consumer<? super List<String>> f) {
        ScalaInterop.valueNotNull("f", f);
        this.getCleanLines().forEach(f);
    }

    public void print() {
        this.fiGureOps.print();
    }

    @NonNull
    public List<String> asList() {
        return CollectionConverters.SeqHasAsJava(this.fiGureOps.asSeq()).asJava();
    }

    @NonNull
    public String asString() {
        return this.fiGureOps.asString();
    }
}
