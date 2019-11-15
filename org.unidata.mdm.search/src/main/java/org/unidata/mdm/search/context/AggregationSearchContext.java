package org.unidata.mdm.search.context;

import java.util.Collection;

/**
 * @author Mikhail Mikhailov
 * Aggregation super type.
 */
public interface AggregationSearchContext extends TypedSearchContext {
    /**
     * Gets type.
     * @return type
     */
    AggregationType getAggregationType();
    /**
     * Gets the aggregation name.
     * @return name
     */
    String getName();
    /**
     * Gets sub aggregations.
     * @return sub aggregations
     */
    Collection<AggregationSearchContext> aggregations();
    /**
     * Gets a sub aggregation by name.
     * @param subAggregationName the name
     * @return sub aggregation
     */
    <T extends AggregationSearchContext> T subAggregation(String subAggregationName);
    /**
     * Narrows type.
     * @return self cast to narrowed type
     */
    @SuppressWarnings("unchecked")
    default <T extends AggregationSearchContext> T narrow() {
        return (T) this;
    }
    /**
     * Type of aggregation
     */
    public enum AggregationType {
        NESTED,
        REVERSE_NESTED,
        FILTER,
        FILTERS {
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isMultiBucket() {
                return true;
            }
        },
        TERM {
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isMultiBucket() {
                return true;
            }
        },
        CARDINALITY,
        VALUE_COUNT;
        /**
         * Tells whether this type is a multibucket one.
         * @return true if so, false otherwise.
         */
        public boolean isMultiBucket() {
            return false;
        }
    }
}
