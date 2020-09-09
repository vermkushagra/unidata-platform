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

package com.unidata.mdm.backend.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * The Class CollectionUtils.
 *
 * @author Michael Yashin. Created on 17.04.2015.
 */
public class CollectionUtils {

    /**
	 * Returns a new {@link Collection} containing <tt><i>a</i> - <i>b</i></tt>.
	 * 
	 * For now element cardinality is not handled!
	 *
	 * @param <O>
	 *            the generic type that is able to represent the types contained
	 *            in both input collections.
	 * @param a
	 *            the collection to subtract from, must not be null
	 * @param b
	 *            the collection to subtract, must not be null
	 * @param comparator
	 *            comparator used to compare objects
	 * @return a new collection with the results
	 */
    public static <O> Collection<O> subtract(Iterable<? extends O> a, Iterable<? extends O> b, Comparator<O> comparator) {
        ArrayList<O> result = new ArrayList<>();
        for (O elementA : a) {
            if (!contains(b, elementA, comparator)) {
                result.add(elementA);
            }
        }
        return result;
    }

    /**
	 * Returns a {@link Collection} containing the intersection of the given
	 * {@link Iterable}s.
	 * 
	 * For now element cardinality is not handled!
	 *
	 * @param <O>
	 *            the generic type that is able to represent the types contained
	 *            in both input collections.
	 * @param a
	 *            the collection to intersect, must not be null
	 * @param b
	 *            the collection to intersect, must not be null
	 * @param comparator
	 *            comparator used to compare objects
	 * @return a new collection with the results
	 */
    public static <O> Collection<O> intersect(Iterable<? extends O> a, Iterable<? extends O> b, Comparator<O> comparator) {
        ArrayList<O> result = new ArrayList<>();
        for (O elementA : a) {
            if (contains(b, elementA, comparator)) {
                result.add(elementA);
            }
        }
        return result;
    }

    /**
	 * Returns <code>true</code> if element is contained in collection
	 * 
	 * For now element cardinality is not handled!.
	 *
	 * @param <O>
	 *            the generic type that is able to represent the types contained
	 *            in both input collections.
	 * @param collection
	 *            the collection to search, must not be null
	 * @param object
	 *            the element to search for, must not be null
	 * @param comparator
	 *            comparator used to compare objects
	 * @return <code>true</code> if element is contained in collection
	 */
    public static <O> boolean contains(Iterable<? extends O> collection, O object, Comparator<O> comparator) {
        for (O element : collection) {
            if (comparator.compare(element, object) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
	 * Find comparable.
	 *
	 * @param <O>
	 *            the generic type
	 * @param collection
	 *            the collection
	 * @param object
	 *            the object
	 * @param comparator
	 *            the comparator
	 * @return the o
	 */
    public static <O> O findComparable(Iterable<? extends O> collection, O object, Comparator<O> comparator) {
        for (O element : collection) {
            if (comparator.compare(element, object) == 0) {
                return element;
            }
        }
        return null;
    }

    /**
	 * Diff intersect.
	 *
	 * @param <O>
	 *            the generic type
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @param comparator
	 *            the comparator
	 * @return the pair
	 */
    public static <O> Pair<Collection<O>, Collection<O>> diffIntersect(Iterable<? extends O> a, Iterable<? extends O> b, Comparator<O> comparator) {
        ArrayList<O> updated = new ArrayList<>();
        ArrayList<O> unchanged = new ArrayList<>();
        for (O elementA : a) {
            O elementB = findComparable(b, elementA, comparator);
            if (elementB != null) {
                if (equals(elementA, elementB)) {
                    unchanged.add(elementA);
                } else {
                    updated.add(elementA);
                }
            }
        }
        return new ImmutablePair<>(updated, unchanged);
    }

    /**
	 * Diff.
	 *
	 * @param <O>
	 *            the generic type
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @param comparator
	 *            the comparator
	 * @return the diff result
	 */
    public static <O> DiffResult<O> diff(Iterable<? extends O> a, Iterable<? extends O> b, Comparator<O> comparator) {
        Collection<O> added = CollectionUtils.subtract(a, b, comparator);
        Collection<O> deleted = CollectionUtils.subtract(b, a, comparator);
        Pair<Collection<O>, Collection<O>> intersectPair = diffIntersect(a, b, comparator);
        DiffResult<O> result = new DiffResult<>();
        result.setAdded(added);
        result.setDeleted(deleted);
        result.setUpdated(intersectPair.getLeft());
        result.setUnchanged(intersectPair.getRight());
        return result;
    }

    /**
	 * Filter.
	 *
	 * @param <T>
	 *            the generic type
	 * @param criteria
	 *            the criteria
	 * @param list
	 *            the list
	 * @return the list
	 */
    public static <T> List<T> filter(Predicate<T> criteria, List<T> list) {
        return list.stream().filter(criteria).collect(Collectors.<T>toList());
    }

    /**
	 * Gets the.
	 *
	 * @param <T>
	 *            the generic type
	 * @param criteria
	 *            the criteria
	 * @param list
	 *            the list
	 * @return the t
	 */
    public static <T> T get(Predicate<T> criteria, List<T> list) {
        return list.stream().filter(criteria).findFirst().get();
    }

    /**
	 * Checks if is present.
	 *
	 * @param <T>
	 *            the generic type
	 * @param criteria
	 *            the criteria
	 * @param list
	 *            the list
	 * @return true, if is present
	 */
    public static <T> boolean isPresent(Predicate<T> criteria, List<T> list) {
        return list.stream().anyMatch(criteria);
    }

    /**
	 * Equals.
	 *
	 * @param o1
	 *            the o1
	 * @param o2
	 *            the o2
	 * @return true, if successful
	 */
    private static boolean equals(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    /**
	 * Safe sub list.
	 *
	 * @param <T>
	 *            the generic type
	 * @param list
	 *            the list
	 * @param fromIndex
	 *            the from index
	 * @param toIndex
	 *            the to index
	 * @return the list
	 */
    public static <T> List<T> safeSubList(List<T> list, int fromIndex, int toIndex) {
        if (list == null) {
            return new ArrayList<>();
        }
        int size = list.size();
        int from = Math.min(size, Math.max(0, fromIndex));
        int to = Math.min(size, toIndex);
        return list.subList(from, to);
    }

}
