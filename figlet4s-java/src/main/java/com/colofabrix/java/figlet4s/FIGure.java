package com.colofabrix.java.figlet4s;

import com.colofabrix.scala.figlet4s.figfont.FIGfont;
import com.colofabrix.scala.figlet4s.figfont.SubColumns;
import java.util.List;

import scala.collection.Seq;
import scala.jdk.CollectionConverters;

public class FIGure {

    private final FIGfont font;
    private final String value;
    private final List<List<String>> columns;

    public FIGure(FIGfont font, String value, List<List<String>> columns) {
        this.font = font;
        this.value = value;
        this.columns = columns;
    }

    public FIGure(com.colofabrix.scala.figlet4s.figfont.FIGure figure) {
        this.font = figure.font();
        this.value = figure.value();

        Iterable<SubColumns> cols2 = figure.columns().map(sc -> CollectionConverters.SeqHasAsJava(sc.value()).asJava());

        // this.columns = figure.columns();
    }

    public FIGfont getFont() {
        return this.font;
    }

    public String getValue() {
        return this.value;
    }

    public List<List<String>> getColumns() {
        return this.columns;
    }
}
