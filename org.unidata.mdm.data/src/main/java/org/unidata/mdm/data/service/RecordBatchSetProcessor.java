package org.unidata.mdm.data.service;

import org.unidata.mdm.data.type.apply.batch.impl.RecordDeleteBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RecordMergeBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RecordUpsertBatchSetAccumulator;

/**
 * @author Mikhail Mikhailov
 * The batch application processor interface.
 */
public interface RecordBatchSetProcessor extends RecordChangeSetProcessor {
    /**
     * Applies upsert batch  set to DB and index.
     * @param bsa batch set accumulator for processing
     */
    void apply(RecordUpsertBatchSetAccumulator bsa);
    /**
     * Applies delete batch  set to DB and index.
     * @param bsa batch set accumulator for processing
     */
    void apply(RecordDeleteBatchSetAccumulator bsa);
    /**
     * Applies merge batch  set to DB and index.
     * @param bsa batch set accumulator for processing
     */
    void apply(RecordMergeBatchSetAccumulator bsa);
}
