package org.unidata.mdm.search.context;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Mikhail Mikhailov
 * Filter aggregation.
 */
public class TermsAggregationRequestContext extends AbstractAggregationRequestContext {
    /**
     * Field for term.
     */
    private final String path;
    /**
     * Max buckets to return (default is 10).
     */
    private final int size;
    /**
     * Min document count for a term to get into a bucket.
     */
    private final int minCount;
    /**
     * Direct values to include.
     */
    private final Object[] includeValues;
    /**
     * Include prefix.
     */
    private final String include;
    /**
     * Direct values to exclude.
     */
    private final Object[] excludeValues;
    /**
     * Exclude prefix.
     */
    private final String exclude;
    /**
     * Collect mode
     */
    private final CollectMode collectMode;
    /**
     * Constructor.
     * @param b the builder
     */
    private TermsAggregationRequestContext(TermsAggregationRequestContextBuilder b) {

        super();

        // Commonn part
        this.entity = b.entity;
        this.name = b.name;
        this.storageId = b.storageId;
        this.subAggregations = b.subAggregations;

        // Private part
        this.path = b.path;
        this.size = b.size;
        this.minCount = b.minCount;
        this.includeValues = b.includeValues;
        this.include = b.include;
        this.excludeValues = b.excludeValues;
        this.exclude = b.exclude;
        this.collectMode = b.collectMode;

    }
    /**
     * {@inheritDoc}
     */
    @Override
    public AggregationType getAggregationType() {
        return AggregationType.TERM;
    }
    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }
    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }
    /**
     * @return the minCount
     */
    public int getMinCount() {
        return minCount;
    }
    /**
     * @return the includeValues
     */
    public Object[] getIncludeValues() {
        return includeValues;
    }
    /**
     * @return the include
     */
    public String getInclude() {
        return include;
    }
    /**
     * @return the excludeValues
     */
    public Object[] getExcludeValues() {
        return excludeValues;
    }
    /**
     * @return the exclude
     */
    public String getExclude() {
        return exclude;
    }
    /**
     * @return the collect node
     */
    public CollectMode getCollectMode() {
        return collectMode;
    }
    /**
     * Builder.
     * @return builder
     */
    public static TermsAggregationRequestContextBuilder builder() {
        return new TermsAggregationRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * The builder.
     */
    public static class TermsAggregationRequestContextBuilder {
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
         * Field path.
         */
        private String path;
        /**
         * Max buckets to return (default is 10).
         */
        private int size = 0;
        /**
         * Min document count for a term to get into a bucket.
         */
        private int minCount = 1;
        /**
         * Direct values to include.
         */
        private Object[] includeValues;
        /**
         * Include prefix.
         */
        private String include;
        /**
         * Direct values to exclude.
         */
        private Object[] excludeValues;
        /**
         * Exclude prefix.
         */
        private String exclude;

        /**
         * Collect mode
         */
        private CollectMode collectMode;
        /**
         * Constructor.
         */
        private TermsAggregationRequestContextBuilder() {
            super();
        }
        /**
         * @param entityName - entity name
         * @return self
         */
        public TermsAggregationRequestContextBuilder entity(String entityName) {
            this.entity = entityName;
            return this;
        }
        /**
         * Overrides default storage id.
         * @param storageId the storage id to use
         * @return self
         */
        public TermsAggregationRequestContextBuilder storageId(String storageId) {
            this.storageId = storageId;
            return this;
        }
        /**
         * @param name - aggregation name
         * @return self
         */
        public TermsAggregationRequestContextBuilder name(String name) {
            this.name = name;
            return this;
        }
        /**
         * @param path - term field
         * @return self
         */
        public TermsAggregationRequestContextBuilder path(String path) {
            this.path = path;
            return this;
        }
        /**
         * @param agg - sub aggregation
         * @return self
         */
        public TermsAggregationRequestContextBuilder subAggregation(AggregationSearchContext agg) {

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
        public TermsAggregationRequestContextBuilder subAggregations(Collection<AggregationSearchContext> aggs) {

            if (CollectionUtils.isEmpty(aggs)) {
                return this;
            }

            for (AggregationSearchContext agg : aggs) {
                subAggregation(agg);
            }

            return this;
        }
        /**
         * @param size - max buckets size
         * @return self
         */
        public TermsAggregationRequestContextBuilder size(int size) {
            this.size = size;
            return this;
        }
        /**
         * @param minCount - min documents count
         * @return self
         */
        public TermsAggregationRequestContextBuilder minCount(int minCount) {
            this.minCount = minCount;
            return this;
        }
        /**
         * @param includeValues - direct filter values
         * @return self
         */
        public TermsAggregationRequestContextBuilder includeValues(Object[] includeValues) {
            this.includeValues = includeValues;
            return this;
        }
        /**
         * @param include - include values filter
         * @return self
         */
        public TermsAggregationRequestContextBuilder include(String include) {
            this.include = include;
            return this;
        }
        /**
         * @param excludeValues - direct filter values to exclude
         * @return self
         */
        public TermsAggregationRequestContextBuilder excludeValues(Object[] excludeValues) {
            this.excludeValues = excludeValues;
            return this;
        }
        /**
         * @param exclude - exclude values filter
         * @return self
         */
        public TermsAggregationRequestContextBuilder exclude(String exclude) {
            this.exclude = exclude;
            return this;
        }
        /**
         * @param collectMode - collect mode
         * @return self
         */
        public TermsAggregationRequestContextBuilder collectMode(CollectMode collectMode) {
            this.collectMode = collectMode;
            return this;
        }
        /**
         * Builds a context from this builder.
         * @return new {@link TermsAggregationRequestContext}
         */
        public TermsAggregationRequestContext build() {
            return new TermsAggregationRequestContext(this);
        }
    }

    public enum CollectMode{
        DEPTH_FIRST, BREADTH_FIRST
    }
}
