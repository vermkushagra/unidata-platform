package org.unidata.mdm.data.service.segments.records.batch;

import org.springframework.stereotype.Component;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.batch.impl.RecordUpsertBatchSetAccumulator;
import org.unidata.mdm.system.type.pipeline.batch.BatchedStart;

/**
 * @author Mikhail Mikhailov on Dec 12, 2019
 */
@Component(RecordsUpsertStartExecutor.SEGMENT_ID)
public class RecordsUpsertStartExecutor extends BatchedStart<RecordUpsertBatchSetAccumulator> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[BATCH_RECORD_UPSERT_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".batch.record.upsert.start.description";
    /**
     * Constructor.
     */
    public RecordsUpsertStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, RecordUpsertBatchSetAccumulator.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(RecordUpsertBatchSetAccumulator ctx) {
        // Nothing. Possibly some kind of validation in the fuutre
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(RecordUpsertBatchSetAccumulator ctx) {
        // No subjects for batched segments so far
        return null;
    }
}
