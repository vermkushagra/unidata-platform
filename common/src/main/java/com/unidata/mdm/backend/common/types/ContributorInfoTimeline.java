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
