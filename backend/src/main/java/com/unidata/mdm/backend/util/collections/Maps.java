/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.util.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.commons.lang3.tuple.Pair;

// Remove in Java 9+
public final class Maps {
    private Maps() {
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        return buildMap(Pair.of(k1, v1), Pair.of(k2, v2));
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        return buildMap(Pair.of(k1, v1), Pair.of(k2, v2), Pair.of(k3, v3));
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return buildMap(Pair.of(k1, v1), Pair.of(k2, v2), Pair.of(k3, v3), Pair.of(k4, v4));
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return buildMap(Pair.of(k1, v1), Pair.of(k2, v2), Pair.of(k3, v3), Pair.of(k4, v4), Pair.of(k5, v5));
    }

    /*public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return buildMap(Pair.of(k1, v1), Pair.of(k2, v2));
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        return buildMap(Pair.of(k1, v1), Pair.of(k2, v2));
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
        return buildMap(Pair.of(k1, v1), Pair.of(k2, v2));
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
        return buildMap(Pair.of(k1, v1), Pair.of(k2, v2));
    }*/

    @SafeVarargs
    private static <K, V> Map<K, V> buildMap(Pair<K, V> ... kvs) {
        final Map<K, V> map = new HashMap<>();
        for (Pair<K, V> kv : kvs) {
            map.put(kv.getKey(), kv.getValue());
        }
        return Collections.unmodifiableMap(map);
    }

    public static <U, K, V> U fold(Map<K, V> map, U identity, BiFunction<U, Map.Entry<K, V>, U> folder) {
        U result = identity;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            result = folder.apply(result, entry);
        }
        return result;
    }
}
