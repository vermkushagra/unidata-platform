package com.unidata.mdm.backend.common.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;

import com.unidata.mdm.backend.common.keys.Keys;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.RelationKeys;

/**
 * @author Mikhail Mikhailov
 * The timeline.
 */
public abstract class Timeline<T extends Calculable> implements Iterable<TimeInterval<T>> {
    /**
     * Published state
     */
    protected Keys keys;
    /**
     * Intervals.
     */
    protected final List<TimeInterval<T>> intervals = new ArrayList<>(8);
    /**
     * Constructor.
     */
    protected Timeline() {
        super();
    }
    /**
     * @return the keys
     */
    @SuppressWarnings("unchecked")
    public<V extends Keys> V getKeys() {
        return (V) keys;
    }
    /**
     * @param keys the keys to set
     */
    public void setKeys(Keys keys) {
        this.keys = keys;
    }
    /**
     * @return the pending
     */
    public abstract boolean isPending();
    /**
     * @return the published
     */
    public abstract boolean isPublished();
    /**
     * Tells whether this timeline is empty.
     * @return true, if empty, false otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }
    /**
     * Adds an interval to collection.
     * @param v the object to add
     */
    public void add(TimeInterval<T> v) {
        intervals.add(v);
    }
    /**
     * Adds a collection of interval objects.
     * @param v collection
     */
    public void addAll(List<TimeInterval<T>> v) {
        for (TimeInterval<T> h : v) {
            add(h);
        }
    }
    /**
     * Gets a time interval object at index i.
     * @param i the index
     * @return interval
     */
    public TimeInterval<T> get(int i) {

        // Suppress AIOOB
        if (i >= intervals.size()) {
            return null;
        }

        return intervals.get(i);
    }
    /**
     * Removes a caclulable object at index i.
     * @param i the index
     * @return calculable
     */
    public TimeInterval<T> remove(int i) {

        // Suppress AIOOB
        if (i >= intervals.size()) {
            return null;
        }

        return intervals.remove(i);
    }
    /**
     * Removes all collected intervals without resetting state (keys).
     * @return contained elements
     */
    public List<TimeInterval<T>> removeAll() {
        List<TimeInterval<T>> elements = new ArrayList<>(intervals);
        intervals.clear();
        return elements;
    }
    /**
     * Selects interval, which includes the given date.
     * @param asOf the date, null is treated as current timestamp.
     * @return
     */
    public TimeInterval<T> selectAsOf(Date asOf) {

        if (isEmpty()) {
            return null;
        }

        Date point = asOf == null ? new Date() : asOf;
        for (TimeInterval<T> interval : intervals) {
            if (interval.isInRange(point)) {
                return interval;
            }
        }

        return null;
    }
    /**
     * Selects a timeline segment by given boundary.
     * @param from the from date
     * @param to the to date
     * @return sub segment
     */
    public List<TimeInterval<T>> selectBy(Date from, Date to) {

        if (CollectionUtils.isEmpty(intervals)) {
            return Collections.emptyList();
        }

        int left = from == null ? 0 : -1;
        int right = to == null ? size() - 1 : -1;
        for (int i = 0; (left == -1 || right == -1) && i < size(); i++) {

            TimeInterval<T> interval = get(i);
            if (left == -1 && interval.isInRange(from)) {
                left = i;
            }

            if (right == -1 && interval.isInRange(to)) {
                right = i;
            }
        }

        if (left != -1 && right != -1) {
            return new ArrayList<>(intervals.subList(left, right + 1));
        }

        return Collections.emptyList();
    }
    /**
     * Reduces interval by given boundary.
     * @param from the from date
     * @param to the to date
     */
    public void reduceBy(Date from, Date to) {

        List<TimeInterval<T>> reduced = selectBy(from, to);
        if (CollectionUtils.isNotEmpty(reduced) && reduced.size() < intervals.size()) {
            intervals.clear();
            intervals.addAll(reduced);
        }
    }
    /**
     * Clears state - intervals and keys.
     */
    public void clear() {
        intervals.clear();
        keys = null;
    }
    /**
     * Returns contributors size.
     * @return number of elements
     */
    public int size() {
        return intervals.size();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<TimeInterval<T>> iterator() {
        return intervals.listIterator();
    }
    /**
     * Opens a stream of intervals.
     * @return stream
     */
    public Stream<TimeInterval<T>> stream() {
        return intervals.stream();
    }
    /**
     * Creates record timeline.
     * @param keys record keys
     * @param intervals record intervals
     * @return timeline
     */
    @SuppressWarnings("unchecked")
    public static<T extends Calculable> Timeline<T> of(RecordKeys keys, List<TimeInterval<OriginRecord>> intervals) {
        return (Timeline<T>) new RecordTimeline()
                .withKeys(keys)
                .withTimeIntervals(intervals);
    }
    /**
     * Creates relation timeline.
     * @param keys relation keys
     * @param intervals relation intervals
     * @return timeline
     */
    @SuppressWarnings("unchecked")
    public static<T extends Calculable> Timeline<T> of(RelationKeys keys, List<TimeInterval<OriginRelation>> intervals) {
        return (Timeline<T>) new RelationTimeline()
                .withKeys(keys)
                .withTimeIntervals(intervals);
    }
    /**
     * Creates CI timeline.
     * @param keys relation keys
     * @param intervals relation intervals
     * @return timeline
     */
    @SuppressWarnings("unchecked")
    public static<T extends Calculable> Timeline<T> of(Keys keys, List<TimeInterval<TimeIntervalContributorInfo>> intervals) {
        return (Timeline<T>) new ContributorInfoTimeline()
                .withKeys(keys)
                .withTimeIntervals(intervals);
    }
}
