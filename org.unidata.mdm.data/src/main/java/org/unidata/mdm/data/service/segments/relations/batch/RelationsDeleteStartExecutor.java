package org.unidata.mdm.data.service.segments.relations.batch;

import org.springframework.stereotype.Component;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.batch.impl.RelationDeleteBatchSetAccumulator;
import org.unidata.mdm.system.type.pipeline.batch.BatchedStart;

/**
 * @author Mikhail Mikhailov on Dec 12, 2019
 */
@Component(RelationsDeleteStartExecutor.SEGMENT_ID)
public class RelationsDeleteStartExecutor extends BatchedStart<RelationDeleteBatchSetAccumulator> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[BATCH_RELATIONS_DELETE_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".batch.relations.delete.start.description";
    /**
     * Constructor.
     */
    public RelationsDeleteStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, RelationDeleteBatchSetAccumulator.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(RelationDeleteBatchSetAccumulator ctx) {
        // Nothing has to be done here so far
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(RelationDeleteBatchSetAccumulator ctx) {
        // No subjects for batched segments so far
        return null;
    }
}
