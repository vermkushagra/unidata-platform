package com.unidata.mdm.backend.common.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;

/**
 * @author Mikhail Mikhailov
 * Reverse nested.
 */
public class ReverseNestedAggregationRequestContext extends AbstractAggregationRequestContext {
    /**
     * Nested path.
     */
    private final String path;
    /**
     * Constructor.
     * @param b the builder
     */
    private ReverseNestedAggregationRequestContext(ReverseNestedAggregationRequestContextBuilder b) {

        super();

        // Commonn part
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
        return AggregationType.REVERSE_NESTED;
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
    public static ReverseNestedAggregationRequestContextBuilder builder() {
        return new ReverseNestedAggregationRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * The builder.
     */
    public static class ReverseNestedAggregationRequestContextBuilder {
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
        private String path;
        /**
         * Constructor.
         */
        private ReverseNestedAggregationRequestContextBuilder() {
            super();
        }
        /**
         * @param entityName - entity name
         * @return self
         */
        public ReverseNestedAggregationRequestContextBuilder entity(String entityName) {
            this.entity = entityName;
            return this;
        }
        /**
         * Overrides default storage id.
         * @param storageId the storage id to use
         * @return self
         */
        public ReverseNestedAggregationRequestContextBuilder storageId(String storageId) {
            this.storageId = storageId;
            return this;
        }
        /**
         * @param name - aggregation name
         * @return self
         */
        public ReverseNestedAggregationRequestContextBuilder name(String name) {
            this.name = name;
            return this;
        }
        /**
         * @param path - aggregation path
         * @return self
         */
        public ReverseNestedAggregationRequestContextBuilder path(String path) {
            this.path = path;
            return this;
        }
        /**
         * @param agg - sub aggregation
         * @return self
         */
        public ReverseNestedAggregationRequestContextBuilder subAggregation(AggregationRequestContext agg) {

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
         * @param agg - sub aggregation
         * @return self
         */
        public ReverseNestedAggregationRequestContextBuilder subAggregations(Collection<AggregationRequestContext> aggs) {

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
         * @return new {@link ReverseNestedAggregationRequestContext}
         */
        public ReverseNestedAggregationRequestContext build() {
            return new ReverseNestedAggregationRequestContext(this);
        }
    }
}
