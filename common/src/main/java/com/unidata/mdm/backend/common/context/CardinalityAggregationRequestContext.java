package com.unidata.mdm.backend.common.context;

import java.util.Collections;

/**
 * @author Mikhail Mikhailov
 * Cardinality aggregation.
 */
public class CardinalityAggregationRequestContext extends AbstractAggregationRequestContext {
    /**
     * Nested path.
     */
    private final String path;
    /**
     * Constructor.
     * @param b the builder
     */
    private CardinalityAggregationRequestContext(CardinalityAggregationRequestContextBuilder b) {

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
        return AggregationType.CARDINALITY;
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
    public static CardinalityAggregationRequestContextBuilder builder() {
        return new CardinalityAggregationRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * The builder.
     */
    public static class CardinalityAggregationRequestContextBuilder {
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
        private CardinalityAggregationRequestContextBuilder() {
            super();
        }
        /**
         * @param entityName - entity name
         * @return self
         */
        public CardinalityAggregationRequestContextBuilder entity(String entityName) {
            this.entity = entityName;
            return this;
        }
        /**
         * Overrides default storage id.
         * @param storageId the storage id to use
         * @return self
         */
        public CardinalityAggregationRequestContextBuilder storageId(String storageId) {
            this.storageId = storageId;
            return this;
        }
        /**
         * @param name - aggregation name
         * @return self
         */
        public CardinalityAggregationRequestContextBuilder name(String name) {
            this.name = name;
            return this;
        }
        /**
         * @param path - aggregation path
         * @return self
         */
        public CardinalityAggregationRequestContextBuilder path(String path) {
            this.path = path;
            return this;
        }
        /**
         * Builds a context from this builder.
         * @return new {@link CardinalityAggregationRequestContext}
         */
        public CardinalityAggregationRequestContext build() {
            return new CardinalityAggregationRequestContext(this);
        }
    }
}
