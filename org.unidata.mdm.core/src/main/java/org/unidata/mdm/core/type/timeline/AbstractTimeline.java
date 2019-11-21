package org.unidata.mdm.core.type.timeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.core.type.calculables.Calculable;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.keys.Keys;
import org.unidata.mdm.core.type.timeline.impl.RevisionSlider;
import org.unidata.mdm.core.type.timeline.impl.TimelineIterator;

/**
 * @author Mikhail Mikhailov
 * Common functionality for timeline.
 */
public abstract class AbstractTimeline<C extends Calculable> implements Timeline<C> {
    /**
     * Published state
     */
    protected Keys<?, ?> keys;
    /**
     * Intervals.
     */
    protected final List<TimeInterval<C>> intervals = new ArrayList<>(8);
    /**
     * Constructor.
     * @param keys the keys to hold
     */
    protected AbstractTimeline(Keys<?, ?> keys) {
        super();
        this.keys = keys;
    }
    /**
     * Constructor.
     */
    protected AbstractTimeline(Keys<?, ?> keys, Collection<TimeInterval<C>> input) {
        this(keys);
        this.intervals.addAll(input);
    }
    /**
     * Constructor.
     */
    protected AbstractTimeline(Keys<?, ?> keys, List<CalculableHolder<C>> input) {
        this(keys);
        this.input(input);
    }
    /**
     * @return the keys
     */
    @Override
    @SuppressWarnings("unchecked")
    public<V extends Keys<?, ?>> V getKeys() {
        return (V) keys;
    }
    /**
     * @param keys the keys to set
     */
    @Override
    public void setKeys(Keys<?, ?> keys) {
        this.keys = keys;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPublished() {
        return getKeys() != null && getKeys().isPublished();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPending() {
        return getKeys() != null && getKeys().isPending() || intervals.stream().anyMatch(TimeInterval::isPending);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public TimeInterval<C> first() {
        return isEmpty() ? null : get(0);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public TimeInterval<C> last() {
        return isEmpty() ? null : get(size() - 1);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public TimeInterval<C> get(int i) {

        // Suppress AIOOB
        if (i >= intervals.size()) {
            return null;
        }

        return intervals.get(i);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void add(TimeInterval<C> interval) {
        intervals.add(interval);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(int i) {
        // Suppress AIOOB
        if (i >= intervals.size()) {
            return;
        }

        intervals.remove(i);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        intervals.clear();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public TimeInterval<C> selectAsOf(Date asOf) {

        if (isEmpty()) {
            return null;
        }

        Date point = asOf == null ? new Date() : asOf;
        for (TimeInterval<C> interval : intervals) {
            if (interval.isInRange(point)) {
                return interval;
            }
        }

        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeInterval<C>> selectBy(Date from, Date to) {

        if (CollectionUtils.isEmpty(intervals)) {
            return Collections.emptyList();
        } else if (from == null && to == null) {
            return new ArrayList<>(intervals);
        }

        List<TimeInterval<C>> result = new ArrayList<>();
        for (int i = 0; i < size(); i++) {

            TimeInterval<C> interval = get(i);
            boolean left = from == null || interval.getValidTo() == null || from.compareTo(interval.getValidTo()) < 0;
            boolean right = to == null || interval.getValidFrom() == null || to.compareTo(interval.getValidFrom()) > 0;

            if (left && right) {
                result.add(interval);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeInterval<C>> getAll() {
        return Collections.unmodifiableList(intervals);
    }

    @Override
    public boolean isFullCovered(Date from, Date to, boolean onlyActive) {
        boolean covered = false;
        List<TimeInterval<C>> periods = new ArrayList<>(intervals.stream()
                .filter(interval -> !onlyActive || interval.isActive())
                .collect(Collectors.toList()));

        periods.sort((o1, o2) -> {

            if (o1.getValidFrom() == null && o2.getValidFrom() == null) {
                return 0;
            } else if (o1.getValidFrom() == null) {
                return -1;
            } else if (o2.getValidFrom() == null) {
                return 1;
            }

            return o1.getValidFrom().compareTo(o2.getValidFrom());
        });

        Date checkFrom = from;
        Date checkTo = to;
        long delta = TimeUnit.MILLISECONDS.toMillis(1);

        for (TimeInterval<C> period : periods) {
            if (period.getValidFrom() != null
                    && (checkFrom == null || period.getValidFrom().getTime() > checkFrom.getTime() + delta)) {
                break;
            }
            if (period.getValidTo() == null
                    || (checkTo != null && checkTo.getTime() - delta < period.getValidTo().getTime())) {
                covered = true;
                break;
            }
            if (checkFrom == null || checkFrom.getTime() < period.getValidTo().getTime())
                checkFrom = period.getValidTo();
        }

        return covered;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return intervals.size();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<TimeInterval<C>> iterator() {
        return intervals.listIterator();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<TimeInterval<C>> stream() {
        return intervals.stream();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Timeline<C> merge(TimeInterval<C> box) {

        if (Objects.isNull(box) || box.isEmpty()) {
            return this;
        }

        RevisionSlider<C> slider = new RevisionSlider<>(this.size() + 1);
        if (box instanceof MutableTimeInterval) {
            slider.add(((MutableTimeInterval<C>) box).toContent());
        } else {
            slider.add(box.toList());
        }

        return of(getKeys(), new ArrayList<>(slider.toMergeCollection()));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Timeline<C> merge(Timeline<C> other) {

        if (Objects.isNull(other) || other.isEmpty()) {
            return this;
        }

        RevisionSlider<C> slider = new RevisionSlider<>(this.size() + other.size());
        TimeInterval<C> it;
        for (int i = 0; i < this.size(); i++) {

            it = this.get(i);
            if (it instanceof MutableTimeInterval) {
                slider.add(((MutableTimeInterval<C>) it).toContent());
            } else {
                slider.add(it.toList());
            }
        }

        for (int i = 0; i < other.size(); i++) {

            it = other.get(i);
            if (it instanceof MutableTimeInterval) {
                slider.add(((MutableTimeInterval<C>) it).toContent());
            } else {
                slider.add(it.toList());
            }
        }

        return of(getKeys(), new ArrayList<>(slider.toMergeCollection()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timeline<C> reduceBy(Date from, Date to) {

        AbstractTimeline<C> result = of(getKeys());

        List<TimeInterval<C>> reduced = selectBy(from, to);
        if (CollectionUtils.isNotEmpty(reduced)) {
            result.intervals.addAll(reduced);
        }

        return result;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Timeline<C> reduceAsOf(Date asOf) {

        AbstractTimeline<C> result = of(getKeys());

        TimeInterval<C> selected = selectAsOf(asOf);
        if (Objects.nonNull(selected)) {
            result.intervals.add(selected);
        }

        return result;
    }
    /**
     * Adds intervals to timeline using the given input.
     * @param values the input to process
     */
    protected void input(List<CalculableHolder<C>> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            for (TimelineIterator<C> ti = new TimelineIterator<>(factory(), values); ti.hasNext(); ) {
                this.intervals.add(ti.next());
            }
        }
    }
    /**
     * Creates a new timeline instance of a particular type.
     * @param keys the keys to use
     * @return new timeline
     */
    protected abstract AbstractTimeline<C> of(Keys<?, ?> keys);
    /**
     * Creates a new timeline instance of a particular type.
     * @param keys the keys to use
     * @param intervals the intervals to hold
     * @return new timeline
     */
    protected abstract AbstractTimeline<C> of(Keys<?, ?> keys, Collection<TimeInterval<C>> intervals);
    /**
     * Creates a new timeline instance of a particular type.
     * @param keys the keys to use
     * @param input the input to hold
     * @return new timeline
     */
    protected abstract AbstractTimeline<C> of(Keys<?, ?> keys, List<CalculableHolder<C>> input);
    /**
     * Gets the time interval factory for this calculable type.
     * @return {@linkplain TimeIntervalFactory}
     */
    protected abstract TimeIntervalFactory<C> factory();
}
