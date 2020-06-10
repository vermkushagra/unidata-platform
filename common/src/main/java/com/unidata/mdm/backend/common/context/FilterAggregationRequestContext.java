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

package com.unidata.mdm.backend.common.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;

import com.unidata.mdm.backend.common.search.FormFieldsGroup;

/**
 * @author Mikhail Mikhailov
 * Filter aggregation.
 */
public class FilterAggregationRequestContext extends AbstractAggregationRequestContext {
    /**
     * Fields for filter.
     */
    private final List<FormFieldsGroup> fields;;
    /**
     * Constructor.
     * @param b the builder
     */
    private FilterAggregationRequestContext(FilterAggregationRequestContextBuilder b) {

        super();

        // Commonn part
        this.entity = b.entity;
        this.name = b.name;
        this.storageId = b.storageId;
        this.subAggregations = b.subAggregations;

        // Private part
        this.fields = b.fields;

    }
    /**
     * {@inheritDoc}
     */
    @Override
    public AggregationType getAggregationType() {
        return AggregationType.FILTER;
    }
    /**
     * @return the filds
     */
    public List<FormFieldsGroup> getFields() {
        return fields;
    }
    /**
     * Builder.
     * @return builder
     */
    public static FilterAggregationRequestContextBuilder builder() {
        return new FilterAggregationRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * The builder.
     */
    public static class FilterAggregationRequestContextBuilder {
        /**
         * Sub aggregations.
         */
        private List<AggregationRequestContext> subAggregations;
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
        private List<FormFieldsGroup> fields;
        /**
         * Constructor.
         */
        private FilterAggregationRequestContextBuilder() {
            super();
        }
        /**
         * @param entityName - entity name
         * @return self
         */
        public FilterAggregationRequestContextBuilder entity(String entityName) {
            this.entity = entityName;
            return this;
        }
        /**
         * Overrides default storage id.
         * @param storageId the storage id to use
         * @return self
         */
        public FilterAggregationRequestContextBuilder storageId(String storageId) {
            this.storageId = storageId;
            return this;
        }
        /**
         * @param name - aggregation name
         * @return self
         */
        public FilterAggregationRequestContextBuilder name(String name) {
            this.name = name;
            return this;
        }
        /**
         * @param fields - aggregation filter
         * @return self
         */
        public FilterAggregationRequestContextBuilder fields(List<FormFieldsGroup> fields) {
            this.fields = fields;
            return this;
        }
        /**
         * @param agg - sub aggregation
         * @return self
         */
        public FilterAggregationRequestContextBuilder subAggregation(AggregationRequestContext agg) {

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
        public FilterAggregationRequestContextBuilder subAggregations(Collection<AggregationRequestContext> aggs) {

            if (CollectionUtils.isEmpty(aggs)) {
                return this;
            }

            for (AggregationRequestContext agg : aggs) {
                subAggregation(agg);
            }

            return this;
        }
        /**
         * Builds a context from this builder.
         * @return new {@link FilterAggregationRequestContext}
         */
        public FilterAggregationRequestContext build() {
            return new FilterAggregationRequestContext(this);
        }
    }
}
