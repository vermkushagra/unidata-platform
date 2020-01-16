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

package org.unidata.mdm.data.type.apply.batch.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.type.apply.RelationUpsertChangeSet;
import org.unidata.mdm.data.type.data.OriginRelation;


/**
 * @author Mikhail Mikhailov
 * Simple relation batch set - objects to process a relation.
 */
public final class RelationUpsertBatchSet extends RelationUpsertChangeSet {
    /**
     * Relations accumulator.
     */
    private final RelationUpsertBatchSetAccumulator accumulator;

    private Map<String, List<Timeline<OriginRelation>>> collectedReferenceTimelines;
    /**
     * Constructor.
     * @param accumulator
     */
    public RelationUpsertBatchSet(RelationUpsertBatchSetAccumulator accumulator) {
        super();
        this.accumulator = accumulator;
    }
    /**
     * @return the accumulator
     */
    public RelationUpsertBatchSetAccumulator getRelationsAccumulator() {
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
