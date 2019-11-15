package org.unidata.mdm.data.type.apply.batch.impl;

import org.unidata.mdm.data.type.apply.RelationDeleteChangeSet;

/**
 * @author Mikhail Mikhailov
 * Simple relation batch set - objects to process a relation.
 */
public final class RelationDeleteBatchSet extends RelationDeleteChangeSet {
    /**
     * Relations accumulator.
     */
    private final RelationDeleteBatchSetAccumulator accumulator;
    /**
     * Constructor.
     * @param accumulator
     */
    public RelationDeleteBatchSet(RelationDeleteBatchSetAccumulator accumulator) {
        super();
        this.accumulator = accumulator;
    }
    /**
     * @return the accumulator
     */
    public RelationDeleteBatchSetAccumulator getRelationsAccumulator() {
        return accumulator;
    }
}
