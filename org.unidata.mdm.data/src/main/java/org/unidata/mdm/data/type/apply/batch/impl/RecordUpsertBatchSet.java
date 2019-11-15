package org.unidata.mdm.data.type.apply.batch.impl;

import org.unidata.mdm.data.type.apply.RecordUpsertChangeSet;

/**
 * @author Mikhail Mikhailov
 * Simple record batch set - objects needed, for a record to be upserted in a batched fashion.
 */
public final class RecordUpsertBatchSet extends RecordUpsertChangeSet {
    /**
     * Accumulator link.
     */
    private RecordUpsertBatchSetAccumulator accumulator;
    /**
     * Constructor.
     * @param accumulator link to accumulator.
     */
    public RecordUpsertBatchSet(RecordUpsertBatchSetAccumulator accumulator) {
        super();
        this.accumulator = accumulator;
    }
    /**
     * @return the accumulator
     */
    public RecordUpsertBatchSetAccumulator getRecordsAccumulator() {
        return accumulator;
    }
}
