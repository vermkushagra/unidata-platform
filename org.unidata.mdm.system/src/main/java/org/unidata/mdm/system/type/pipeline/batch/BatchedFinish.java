package org.unidata.mdm.system.type.pipeline.batch;

import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.PipelineInput;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * @author Mikhail Mikhailov on Oct 2, 2019
 */
public abstract class BatchedFinish<I extends PipelineInput, O extends PipelineOutput> extends Finish<I, O> {
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
