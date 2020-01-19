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

package org.unidata.mdm.data.type.timeline;

import java.util.Collection;
import java.util.List;

import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.keys.Keys;
import org.unidata.mdm.core.type.timeline.AbstractTimeline;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.TimeIntervalFactory;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.keys.RelationKeys;

/**
 * @author Mikhail Mikhailov
 * Records timeline.
 */
public class RelationTimeline extends AbstractTimeline<OriginRelation> {
    /**
     * The relation time interval factory.
     */
    public static final TimeIntervalFactory<OriginRelation> INTERVAL_FACTORY = RelationTimeInterval::new;
    /**
     * Constructor.
     * @param keys the keys to use
     */
    public RelationTimeline(RelationKeys keys) {
        super(keys);
    }
    /**
     * Constructor.
     */
    public RelationTimeline(RelationKeys keys, Collection<TimeInterval<OriginRelation>> intervals) {
        super(keys, intervals);
    }
    /**
     * Constructor.
     * @param keys the keys
     * @param input the raw input
     */
    public RelationTimeline(RelationKeys keys, List<CalculableHolder<OriginRelation>> input) {
        super(keys, input);
    }
    /**
     * Fluent keys method.
     *
     * @param v keys value
     * @return self
     */
    public RelationTimeline withKeys(RelationKeys v) {
        keys = v;
        return this;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractTimeline<OriginRelation> of(Keys<?, ?> keys) {
        return new RelationTimeline((RelationKeys) keys);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractTimeline<OriginRelation> of(Keys<?, ?> keys, Collection<TimeInterval<OriginRelation>> intervals) {
        return new RelationTimeline((RelationKeys) keys, intervals);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractTimeline<OriginRelation> of(Keys<?, ?> keys, List<CalculableHolder<OriginRelation>> input) {
        return new RelationTimeline((RelationKeys) keys, input);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected TimeIntervalFactory<OriginRelation> factory() {
        return INTERVAL_FACTORY;
    }
}
