package org.unidata.mdm.data.type.apply.batch.impl;

import org.unidata.mdm.data.type.apply.RecordDeleteChangeSet;

/**
 * @author Mikhail Mikhailov
 *         Record delete batch set.
 */
public class RecordDeleteBatchSet extends RecordDeleteChangeSet {
    /**
     * Accumulator.
     */
    private RecordDeleteBatchSetAccumulator accumulator;
    /**
     * Constructor.
     *
     * @param accumulator
     */
    public RecordDeleteBatchSet(RecordDeleteBatchSetAccumulator accumulator) {
        super();
        this.accumulator = accumulator;
    }
    /**
     * @return the accumulator
     */
    public RecordDeleteBatchSetAccumulator getRecordsAccumulator() {
        return accumulator;
    }
}
