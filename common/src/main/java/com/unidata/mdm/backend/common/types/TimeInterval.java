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

package com.unidata.mdm.backend.common.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.data.CalculableHolder;

/**
 * @author Mikhail Mikhailov
 * The basic time interval abstraction.
 */
public abstract class TimeInterval<T> implements Iterable<CalculableHolder<T>> {
    /**
     * Timeline max period.id.
     */
    private static final long TIMELINE_MAX_PERIOD_ID = 9223372036825200000L;
    /**
     * @author Mikhail Mikhailov
     * Type of the time interval.
     */
    public enum TimeIntervalType {
        /**
         * Short info.
         */
        INFO,
        /**
         * Record data along with interval info.
         */
        RECORD,
        /**
         * Relation data aong with interval info.
         */
        RELATION
    };
    /**
     * Contributors.
     */
    protected final List<CalculableHolder<T>> contributors = new ArrayList<>(8);
    /**
     * Valid from.
     */
    protected Date validFrom;
    /**
     * Valid to.
     */
    protected Date validTo;
    /**
     * Interval is active or not.
     */
    protected boolean active;
    /**
     * Interval isin pending state.
     */
    protected boolean pending;
    /**
     * The calculation result, if applicable.
     */
    protected CalculationResult<T> calculationResult;
    /**
     * Constructor.
     */
    protected TimeInterval() {
        super();
    }
    /**
     * @return the validFrom
     */
    public Date getValidFrom() {
        return validFrom;
    }
    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }
    /**
     * @return the validTo
     */
    public Date getValidTo() {
        return validTo;
    }
    /**
     * @param validTo the validTo to set
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }
    /**
     * Tells whether this interval is active or not.
     * @return true, if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * @return the result
     */
    @SuppressWarnings("unchecked")
    public<V extends CalculationResult<T>> V getCalculationResult() {
        return (V) calculationResult;
    }
    /**
     * @param result the result to set
     */
    public void setCalculationResult(CalculationResult<T> result) {
        this.calculationResult = result;
    }
    /**
     * Tells whether this interval is in pending state or not.
     * @return true, if active, false otherwise
     */
    public boolean isPending() {
        return pending;
    }
    /**
     * Tells whether the given date is within range of this time interval.
     * @param asOf the date to check interval against. Must not be null
     * @return true, if included, false otherwise
     */
    public boolean isInRange(@Nonnull Date asOf) {
        boolean left = validFrom == null || validFrom.before(asOf) || validFrom.getTime() == asOf.getTime();
        boolean right = validTo == null || validTo.after(asOf) || validTo.getTime() == asOf.getTime();
        return left && right;
    }
    /**
     * Returns true, if this interval contains no contributors.
     * @return true, if this interval contains no contributors, false otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }
    /**
     * Adds an object to store.
     * @param v the object
     */
    public abstract void add(T v);
    /**
     * Gets the timeinterval type.
     * @return type
     */
    public abstract TimeIntervalType getType();
    /**
     * Adds a calculable object to collection.
     * @param v the object to add
     */
    public void add(CalculableHolder<T> v) {
        contributors.add(v);
        if (!pending && v.getApproval() == ApprovalState.PENDING) {
            pending = true;
        }
    }
    /**
     * Adds a collection of calculable objects to interval.
     * @param v collection
     */
    public void addAll(Collection<CalculableHolder<T>> v) {
        for (CalculableHolder<T> h : v) {
            add(h);
        }
    }
    /**
     * Gets a caclulable object at index i.
     * @param i the index
     * @return calculable
     */
    public CalculableHolder<T> get(int i) {

        // Suppress AIOOB
        if (i >= contributors.size()) {
            return null;
        }

        return contributors.get(i);
    }
    /**
     * Removes a caclulable object at index i.
     * @param i the index
     * @return calculable
     */
    public CalculableHolder<T> remove(int i) {

        // Suppress AIOOB
        if (i >= contributors.size()) {
            return null;
        }

        CalculableHolder<T> removed = contributors.remove(i);
        if (pending && removed.getApproval() == ApprovalState.PENDING) {
            pending = contributors.stream().anyMatch(c -> c.getApproval() == ApprovalState.PENDING);
        }

        return removed;
    }
    /**
     * Removes all calculables objects and resets state.
     * @return contained elements
     */
    public Collection<CalculableHolder<T>> removeAll() {
        Collection<CalculableHolder<T>> elements = this.toList();
        clear();
        return elements;
    }
    /**
     * Returns contributors size.
     * @return number of elements
     */
    public int size() {
        return contributors.size();
    }
    /**
     * Clears state.
     */
    public void clear() {
        contributors.clear();
        this.active = false;
        this.pending = false;
    }
    /**
     * Returns calculables array
     * @return array
     */
    public abstract CalculableHolder<T>[] toArray();
    /**
     * Returns value array
     * @return array
     */
    public abstract T[] toValueArray();
    /**
     * Returns unattended calculables list.
     * @return list
     */
    public List<CalculableHolder<T>> toList() {
        return isEmpty() ? Collections.emptyList() : new ArrayList<>(contributors);
    }
    /**
     * Returns the underlaying value list.
     * @return list
     */
    public List<T> toValueList() {
        return isEmpty()
                ? Collections.emptyList()
                : contributors.stream()
                    .map(CalculableHolder::getValue)
                    .collect(Collectors.toList());
    }
    /**
     * Gets the period id.
     * @return the period id
     */
    public long getPeriodId() {
        return Objects.isNull(validTo) ? TIMELINE_MAX_PERIOD_ID : validTo.getTime();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<CalculableHolder<T>> iterator() {
        return contributors.listIterator();
    }
    /**
     * Opens contributors stream.
     * @return stream
     */
    public Stream<CalculableHolder<T>> stream() {
        return contributors.stream();
    }
    /**
     * Record interval creator method.
     * @param contributors
     * @param result
     * @param from
     * @param to
     * @param active
     * @return interval
     */
    @SuppressWarnings("unchecked")
    public static<T> TimeInterval<T> of (Collection<CalculableHolder<OriginRecord>> contributors,
            EtalonRecord result,
            Date from, Date to,
            boolean active) {

        return (TimeInterval<T>) new RecordTimeInterval()
                .withContributors(contributors)
                .withCalculationResult(result)
                .withValidFrom(from)
                .withValidTo(to)
                .withActive(active);
    }

    /**
     * Relation interval creator method.
     * @param contributors
     * @param result
     * @param from
     * @param to
     * @param active
     * @return interval
     */
    @SuppressWarnings("unchecked")
    public static<T> TimeInterval<T> of (Collection<CalculableHolder<OriginRelation>> contributors,
            EtalonRelation result,
            Date from, Date to,
            boolean active) {

        return (TimeInterval<T>) new RelationTimeInterval()
                .withContributors(contributors)
                .withCalculationResult(result)
                .withValidFrom(from)
                .withValidTo(to)
                .withActive(active);
    }

    /**
     * Interval info creator method.
     * @param contributors
     * @param from
     * @param to
     * @param active
     * @return interval
     */
    @SuppressWarnings("unchecked")
    public static<T> TimeInterval<T> of (Collection<CalculableHolder<TimeIntervalContributorInfo>> contributors,
            Date from, Date to,
            boolean active) {

        return (TimeInterval<T>) new ContributorInfoTimeInterval()
                .withContributors(contributors)
                .withValidFrom(from)
                .withValidTo(to)
                .withActive(active);
    }
}
