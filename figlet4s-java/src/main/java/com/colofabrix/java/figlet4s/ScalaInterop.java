package com.colofabrix.java.figlet4s;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import scala.collection.immutable.Seq;
import scala.jdk.CollectionConverters;

class ScalaInterop {

    public static <A> List<A> seqAsList(Seq<A> data) {
        return CollectionConverters.SeqHasAsJava(data).asJava();
    }

    public static <A, B> List<List<B>> nestedSeqAsList(Seq<A> data, Function<A, Seq<B>> extractor) {
        return CollectionConverters
            .SeqHasAsJava(data)
            .asJava()
            .stream()
            .map(sc -> seqAsList(extractor.apply(sc)))
            .collect(Collectors.toList());
    }

    public static <A> void valueNotNull(String name, A value) {
        if (value == null)
            throw new IllegalArgumentException("Argument '" + name + "' cannot be null.");
    }

    public static <A> void listNotNull(String name, List<A> value) {
        valueNotNull(name, value);
        if (value.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("All elements of list " + value + "must not be null");
    }

}
