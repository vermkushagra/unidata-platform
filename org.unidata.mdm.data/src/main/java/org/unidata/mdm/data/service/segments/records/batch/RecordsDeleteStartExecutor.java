package org.unidata.mdm.data.service.segments.records.batch;

import org.springframework.stereotype.Component;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.batch.impl.RecordDeleteBatchSetAccumulator;
import org.unidata.mdm.system.type.pipeline.batch.BatchedStart;

/**
 * @author Mikhail Mikhailov on Dec 12, 2019
 */
@Component(RecordsDeleteStartExecutor.SEGMENT_ID)
public class RecordsDeleteStartExecutor extends BatchedStart<RecordDeleteBatchSetAccumulator> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[BATCH_RECORD_DELETE_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".batch.record.delete.start.description";
    /**
     * Constructor.
     */
    public RecordsDeleteStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, RecordDeleteBatchSetAccumulator.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(RecordDeleteBatchSetAccumulator ctx) {
        // Nothing. Possibly some kind of validation in the fuutre
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(RecordDeleteBatchSetAccumulator ctx) {
        // No subjects for batched segments so far
        return null;
    }
}
