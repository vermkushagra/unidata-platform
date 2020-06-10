package com.unidata.mdm.backend.common.types;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.keys.Keys;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.RelationKeys;

/**
 * @author Mikhail Mikhailov
 * Records timeline.
 */
public class ContributorInfoTimeline extends Timeline<TimeIntervalContributorInfo> {
    /**
     * Constructor.
     */
    public ContributorInfoTimeline() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPublished() {

        Keys thisKeys = getKeys();
        if (thisKeys != null) {
            switch (thisKeys.getType()) {
            case RECORD_KEYS:
                return ((RecordKeys) thisKeys).isPublished();
            case RELATION_KEYS:
                return ((RelationKeys) thisKeys).getFrom().isPublished();
            case CLASSIFIER_KEYS:
                return ((ClassifierKeys) thisKeys).getRecord().isPublished();
            default:
                break;
            }
        }

        return false;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPending() {

        Keys thisKeys = getKeys();
        if (thisKeys != null) {
            switch (thisKeys.getType()) {
            case RECORD_KEYS:
                return ((RecordKeys) thisKeys).isPending();
            case RELATION_KEYS:
                return ((RelationKeys) thisKeys).getFrom().isPending();
            case CLASSIFIER_KEYS:
                return ((ClassifierKeys) thisKeys).getRecord().isPending();
            default:
                break;
            }
        }

        return intervals.stream().anyMatch(TimeInterval::isPending);
    }
    /**
     * Fluent intervals method.
     * @param v collection
     * @return self
     */
    @SuppressWarnings("unchecked")
    public ContributorInfoTimeline withTimeIntervals(TimeInterval<TimeIntervalContributorInfo>... v) {
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
    public ContributorInfoTimeline withTimeIntervals(List<TimeInterval<TimeIntervalContributorInfo>> v) {
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
    public ContributorInfoTimeline withKeys(Keys v) {
        keys = v;
        return this;
    }
}
