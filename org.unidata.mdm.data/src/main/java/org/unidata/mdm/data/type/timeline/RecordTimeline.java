package org.unidata.mdm.data.type.timeline;

import java.util.Collection;
import java.util.List;

import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.keys.Keys;
import org.unidata.mdm.core.type.timeline.AbstractTimeline;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.TimeIntervalFactory;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 * Records timeline.
 */
public class RecordTimeline extends AbstractTimeline<OriginRecord> {
    /**
     * The record time interval factory.
     */
    public static final TimeIntervalFactory<OriginRecord> INTERVAL_FACTORY = RecordTimeInterval::new;
    /**
     * Constructor.
     * @param keys the keys to use
     */
    public RecordTimeline(RecordKeys keys) {
        super(keys);
    }
    /**
     * Constructor.
     * @param keys the keys to use
     * @param intervals intervals to hold
     */
    public RecordTimeline(RecordKeys keys, Collection<TimeInterval<OriginRecord>> intervals) {
        super(keys, intervals);
    }
    /**
     * Constructor.
     * @param keys the keys
     * @param input the raw input
     */
    public RecordTimeline(RecordKeys keys, List<CalculableHolder<OriginRecord>> input) {
        super(keys, input);
    }
    /**
     * Fluent keys method.
     *
     * @param v keys value
     * @return self
     */
    public RecordTimeline withKeys(RecordKeys v) {
        keys = v;
        return this;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractTimeline<OriginRecord> of(Keys<?, ?> keys) {
        return new RecordTimeline((RecordKeys) keys);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractTimeline<OriginRecord> of(Keys<?, ?> keys, Collection<TimeInterval<OriginRecord>> intervals) {
        return new RecordTimeline((RecordKeys) keys, intervals);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractTimeline<OriginRecord> of(Keys<?, ?> keys, List<CalculableHolder<OriginRecord>> input) {
        return new RecordTimeline((RecordKeys) keys, input);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected TimeIntervalFactory<OriginRecord> factory() {
        return INTERVAL_FACTORY;
    }
}
