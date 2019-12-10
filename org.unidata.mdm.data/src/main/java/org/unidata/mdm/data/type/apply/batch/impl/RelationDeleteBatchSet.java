package org.unidata.mdm.data.type.apply.batch.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.type.apply.RelationDeleteChangeSet;
import org.unidata.mdm.data.type.data.OriginRelation;

/**
 * @author Mikhail Mikhailov
 * Simple relation batch set - objects to process a relation.
 */
public final class RelationDeleteBatchSet extends RelationDeleteChangeSet {
    /**
     * Relations accumulator.
     */
    private final RelationDeleteBatchSetAccumulator accumulator;
    /**
     * Cached reference timelines.
     */
    private Map<String, List<Timeline<OriginRelation>>> collectedReferenceTimelines;
    /**
     * Constructor.
     * @param accumulator
     */
    public RelationDeleteBatchSet(RelationDeleteBatchSetAccumulator accumulator) {
        super();
        this.accumulator = accumulator;
    }
    /**
     * @return the accumulator
     */
    public RelationDeleteBatchSetAccumulator getRelationsAccumulator() {
        return accumulator;
    }
    /**
     * @return the cachedRefTimelines
     */
    public List<Timeline<OriginRelation>> findCachedReferenceTimelines(String recordEtalonId, String relationName) {
        List<Timeline<OriginRelation>> result = null;
        if (collectedReferenceTimelines != null) {
            result = collectedReferenceTimelines.get(StringUtils.join(recordEtalonId, "|", relationName));
        }

        return result;
    }
    /**
     * @param cachedReferenceTimelines the cachedRefTimelines to set
     */
    public void addCachedReferenceTimelines(String recordEtalonId, String relationName, List<Timeline<OriginRelation>> cachedReferenceTimelines) {
        if (collectedReferenceTimelines != null) {
            collectedReferenceTimelines.put(StringUtils.join(recordEtalonId, "|", relationName), cachedReferenceTimelines);
        }
    }
    /**
     * @param collectedReferenceTimelines the collectedReferenceTimelines to set
     */
    public void setCollectedReferenceTimelines(Map<String, List<Timeline<OriginRelation>>> collectedReferenceTimelines) {
        this.collectedReferenceTimelines = collectedReferenceTimelines;
    }
}
