package org.unidata.mdm.system.type.pipeline.batch;

import org.unidata.mdm.system.type.pipeline.Finish;

/**
 * @author Mikhail Mikhailov on Oct 2, 2019
 */
public abstract class BatchedFinish<I extends BatchedPipelineInput, O extends BatchedPipelineOutput> extends Finish<I, O> {
    /**
     * Constructor.
     * @param id
     * @param description
     */
    public BatchedFinish(String id, String description, Class<O> outputTypeClass) {
        super(id, description, outputTypeClass);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBatched() {
        return true;
    }
}
