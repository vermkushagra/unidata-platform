package org.unidata.mdm.system.type.pipeline.batch;

import org.unidata.mdm.system.type.pipeline.PipelineInput;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov
 * Starting segment for batched pipelines.
 */
public abstract class BatchedStart<C extends PipelineInput> extends Start<C> {
    /**
     * Constructor.
     * @param id the id
     * @param description the description
     * @param inputTypeClass the input type class
     */
    public BatchedStart(String id, String description, Class<C> inputTypeClass) {
        super(id, description, inputTypeClass);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBatched() {
        return true;
    }
}
