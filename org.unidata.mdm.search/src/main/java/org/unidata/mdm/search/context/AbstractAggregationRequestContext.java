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
