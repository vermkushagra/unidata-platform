package com.unidata.mdm.backend.common.types;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.unidata.mdm.backend.common.keys.RelationKeys;

/**
 * @author Mikhail Mikhailov
 * Records timeline.
 */
public class RelationTimeline extends Timeline<OriginRelation> {
    /**
     * Constructor.
     */
    public RelationTimeline() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPublished() {
        RelationKeys relationKeys = getKeys();
        return relationKeys != null && relationKeys.getFrom().isPublished();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPending() {
        RelationKeys relationKeys = getKeys();
        return relationKeys != null ? relationKeys.getFrom().isPending() : intervals.stream().anyMatch(TimeInterval::isPending);
    }
    /**
     * Fluent intervals method.
     * @param v collection
     * @return self
     */
    @SuppressWarnings("unchecked")
    public RelationTimeline withTimeIntervals(TimeInterval<OriginRelation>... v) {
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
    public RelationTimeline withTimeIntervals(List<TimeInterval<OriginRelation>> v) {
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
    public RelationTimeline withKeys(RelationKeys v) {
        keys = v;
        return this;
    }
}
