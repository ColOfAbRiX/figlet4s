package com.colofabrix.java.figlet4s;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import scala.collection.immutable.Seq;
import scala.jdk.CollectionConverters;

/**
 * Utility class that contains conversion facilities between Scala and Java
 */
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

}
