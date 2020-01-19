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

package org.unidata.mdm.search.context;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Mikhail Mikhailov
 * Common stuff for aggregations.
 */
public abstract class AbstractAggregationRequestContext implements AggregationSearchContext {
    /**
     * Aggregation name.
     */
    protected String name;
    /**
     * Entity name.
     */
    protected String entity;
    /**
     * Storage id.
     */
    protected String storageId;
    /**
     * Sub aggregations.
     */
    protected List<AggregationSearchContext> subAggregations;
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntity() {
        return entity;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStorageId() {
        return storageId;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AggregationSearchContext> aggregations() {
        return Objects.isNull(subAggregations) ? Collections.emptyList() : subAggregations;
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends AggregationSearchContext> T subAggregation(@Nonnull String subAggregationName) {

        for (AggregationSearchContext ctx : aggregations()) {
            if (subAggregationName.equals(ctx.getName())) {
                return (T) ctx;
            }
        }

        return null;
    }
}
