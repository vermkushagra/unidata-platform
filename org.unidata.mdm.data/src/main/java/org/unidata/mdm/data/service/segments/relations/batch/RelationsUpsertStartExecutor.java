package org.unidata.mdm.data.service.segments.relations.batch;

import org.springframework.stereotype.Component;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.batch.impl.RelationUpsertBatchSetAccumulator;
import org.unidata.mdm.system.type.pipeline.batch.BatchedStart;

/**
 * @author Mikhail Mikhailov on Dec 12, 2019
 */
@Component(RelationsUpsertStartExecutor.SEGMENT_ID)
public class RelationsUpsertStartExecutor extends BatchedStart<RelationUpsertBatchSetAccumulator> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[BATCH_RELATIONS_UPSERT_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".batch.relations.upsert.start.description";
    /**
     * Constructor.
     */
    public RelationsUpsertStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, RelationUpsertBatchSetAccumulator.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(RelationUpsertBatchSetAccumulator ctx) {
        // Nothing has to be done here so far
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(RelationUpsertBatchSetAccumulator ctx) {
        // No subjects for batched segments so far
        return null;
    }
}
