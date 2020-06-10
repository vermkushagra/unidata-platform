package com.unidata.mdm.backend.common.context;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * @author Mikhail Mikhailov
 * Common stuff for aggregations.
 */
public abstract class AbstractAggregationRequestContext implements AggregationRequestContext {
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
    protected List<AggregationRequestContext> subAggregations;
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
    public Collection<AggregationRequestContext> aggregations() {
        return Objects.isNull(subAggregations) ? Collections.emptyList() : subAggregations;
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends AggregationRequestContext> T subAggregation(@Nonnull String subAggregationName) {

        for (AggregationRequestContext ctx : aggregations()) {
            if (subAggregationName.equals(ctx.getName())) {
                return (T) ctx;
            }
        }

        return null;
    }
}
