package com.unidata.mdm.backend.util.collections;

import java.util.Collection;
import java.util.function.BiFunction;

public final class Transformations {
    private Transformations() {}

    public static <E, R> R fold(Collection<E> collection, R initial, BiFunction<R, E, R> transformer) {
        R result = initial;
        for (E e : collection) {
            result = transformer.apply(result, e);
        }
        return result;
    }
}
