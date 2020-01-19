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

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Mikhail Mikhailov
 * Nested aggregation.
 */
public class NestedAggregationRequestContext extends AbstractAggregationRequestContext {
    /**
     * Nested path.
     */
    private final String path;
    /**
     * Constructor.
     * @param b the builder
     */
    private NestedAggregationRequestContext(NestedAggregationRequestContextBuilder b) {

        super();

        // Common part
        this.entity = b.entity;
        this.name = b.name;
        this.storageId = b.storageId;
        this.subAggregations = b.subAggregations;

        // Private part
        this.path = b.path;

    }
    /**
     * {@inheritDoc}
     */
    @Override
    public AggregationType getAggregationType() {
        return AggregationType.NESTED;
    }
    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }
    /**
     * Builder.
     * @return builder
     */
    public static NestedAggregationRequestContextBuilder builder() {
        return new NestedAggregationRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * The builder.
     */
    public static class NestedAggregationRequestContextBuilder {
        /**
         * Sub aggregations.
         */
        private List<AggregationSearchContext> subAggregations;
        /**
         * Type to operate on.
         */
        private String entity;
        /**
         * The storage id to use. Overrides the system one.
         */
        private String storageId;
        /**
         * Aggregation name.
         */
        private String name;
        /**
         * Nested path.
         */
        private String path;
        /**
         * Constructor.
         */
        private NestedAggregationRequestContextBuilder() {
            super();
        }
        /**
         * @param entityName - entity name
         * @return self
         */
        public NestedAggregationRequestContextBuilder entity(String entityName) {
            this.entity = entityName;
            return this;
        }
        /**
         * Overrides default storage id.
         * @param storageId the storage id to use
         * @return self
         */
        public NestedAggregationRequestContextBuilder storageId(String storageId) {
            this.storageId = storageId;
            return this;
        }
        /**
         * @param name - aggregation name
         * @return self
         */
        public NestedAggregationRequestContextBuilder name(String name) {
            this.name = name;
            return this;
        }
        /**
         * @param path - aggregation path
         * @return self
         */
        public NestedAggregationRequestContextBuilder path(String path) {
            this.path = path;
            return this;
        }
        /**
         * @param agg - sub aggregation
         * @return self
         */
        public NestedAggregationRequestContextBuilder subAggregation(AggregationSearchContext agg) {

            if (Objects.isNull(agg)) {
                return this;
            }

            if (Objects.isNull(this.subAggregations)) {
                this.subAggregations = new ArrayList<>(2);
            }

            this.subAggregations.add(agg);
            return this;
        }
        /**
         * @param aggs - sub aggregation
         * @return self
         */
        public NestedAggregationRequestContextBuilder subAggregations(Collection<AggregationSearchContext> aggs) {

            if (CollectionUtils.isEmpty(aggs)) {
                return this;
            }

            for (AggregationSearchContext agg : aggs) {
                subAggregation(agg);
            }

            return this;
        }
        /**
         * Builds a context from this builder.
         * @return new {@link NestedAggregationRequestContext}
         */
        public NestedAggregationRequestContext build() {
            return new NestedAggregationRequestContext(this);
        }
    }
}
