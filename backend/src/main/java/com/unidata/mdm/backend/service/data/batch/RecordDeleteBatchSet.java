package com.unidata.mdm.backend.service.data.batch;

/**
 * @author Mikhail Mikhailov
 * Record delete batch set.
 */
public class RecordDeleteBatchSet extends RecordBatchSet {
    /**
     * Accumulator.
     */
    private RecordDeleteBatchSetAccumulator accumulator;
    /**
     * Constructor.
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
