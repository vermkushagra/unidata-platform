package org.unidata.mdm.data.service;

import org.unidata.mdm.data.type.apply.batch.impl.RelationDeleteBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RelationMergeBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RelationUpsertBatchSetAccumulator;

/**
 * @author Mikhail Mikhailov
 * The batch application processor interface.
 */
public interface RelationBatchSetProcessor extends RelationChangeSetProcessor {
    /**
     * Applies upsert relation batch  set to DB and index.
     * @param bsa batch set accumulator for processing
     */
    void apply(RelationUpsertBatchSetAccumulator bsa);

    /**
     * Applies delete relation batch  set to DB and index.
     * @param bsa batch set accumulator for processing
     */
    void apply(RelationDeleteBatchSetAccumulator bsa);

    /**
     * Applies merge relation batch  set to DB and index.
     * @param bsa batch set accumulator for processing
     */
    void apply(RelationMergeBatchSetAccumulator bsa);
}
