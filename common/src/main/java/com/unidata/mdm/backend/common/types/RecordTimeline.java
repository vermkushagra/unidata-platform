package com.unidata.mdm.backend.common.types;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.unidata.mdm.backend.common.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 * Records timeline.
 */
public class RecordTimeline extends Timeline<OriginRecord> {
    /**
     * Constructor.
     */
    public RecordTimeline() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPublished() {
        RecordKeys recordKeys = getKeys();
        return recordKeys != null && recordKeys.isPublished();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPending() {
        RecordKeys recordKeys = getKeys();
        return recordKeys != null ? recordKeys.isPending() : intervals.stream().anyMatch(TimeInterval::isPending);
    }
    /**
     * Fluent intervals method.
     * @param v collection
     * @return self
     */
    @SuppressWarnings("unchecked")
    public RecordTimeline withTimeIntervals(TimeInterval<OriginRecord>... v) {
        if (ArrayUtils.isNotEmpty(v)) {
            for (int i = 0; i < v.length; i++) {
                super.add(v[i]);
            }
        }
        return this;
    }
    /**
     * Fluent intervals method.
     * @param v collection
     * @return self
     */
    public RecordTimeline withTimeIntervals(List<TimeInterval<OriginRecord>> v) {
        if (CollectionUtils.isNotEmpty(v)) {
            super.addAll(v);
        }
        return this;
    }
    /**
     * Fluent keys method.
     * @param v keys value
     * @return self
     */
    public RecordTimeline withKeys(RecordKeys v) {
        keys = v;
        return this;
    }
}
