package org.unidata.mdm.search.context;

import java.util.Collections;

/**
 * @author Dmitrii Kopin
 * Value count aggregation.
 */
public class ValueCountAggregationRequestContext extends AbstractAggregationRequestContext {
    /**
     * Nested path.
     */
    private final String path;
    /**
     * Constructor.
     * @param b the builder
     */
    private ValueCountAggregationRequestContext(ValueCountAggregationRequestContextBuilder b) {

        super();

        // Common part
        this.entity = b.entity;
        this.name = b.name;
        this.storageId = b.storageId;
        this.subAggregations = Collections.emptyList(); // Terminationg metrics aggregation

        // Private part
        this.path = b.path;

    }
    /**
     * {@inheritDoc}
     */
    @Override
    public AggregationType getAggregationType() {
        return AggregationType.VALUE_COUNT;
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
    public static ValueCountAggregationRequestContextBuilder builder() {
        return new ValueCountAggregationRequestContextBuilder();
    }
    /**
     * @author Dmitrii Kopin
     * The builder.
     */
    public static class ValueCountAggregationRequestContextBuilder {
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
        private ValueCountAggregationRequestContextBuilder() {
            super();
        }
        /**
         * @param entityName - entity name
         * @return self
         */
        public ValueCountAggregationRequestContextBuilder entity(String entityName) {
            this.entity = entityName;
            return this;
        }
        /**
         * Overrides default storage id.
         * @param storageId the storage id to use
         * @return self
         */
        public ValueCountAggregationRequestContextBuilder storageId(String storageId) {
            this.storageId = storageId;
            return this;
        }
        /**
         * @param name - aggregation name
         * @return self
         */
        public ValueCountAggregationRequestContextBuilder name(String name) {
            this.name = name;
            return this;
        }
        /**
         * @param path - aggregation path
         * @return self
         */
        public ValueCountAggregationRequestContextBuilder path(String path) {
            this.path = path;
            return this;
        }
        /**
         * Builds a context from this builder.
         * @return new {@link ValueCountAggregationRequestContext}
         */
        public ValueCountAggregationRequestContext build() {
            return new ValueCountAggregationRequestContext(this);
        }
    }
}
