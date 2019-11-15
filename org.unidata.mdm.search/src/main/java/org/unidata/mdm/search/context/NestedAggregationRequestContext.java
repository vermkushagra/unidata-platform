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
