package org.unidata.mdm.system.type.pipeline.batch;

import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.PipelineInput;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * @author Mikhail Mikhailov
 * Batched connector segment.
 */
public abstract class BatchedConnector<I extends PipelineInput, O extends PipelineOutput> extends Connector<I, O> {
    /**
     * Constructor.
     * @param id the id
     * @param description the description
     */
    public BatchedConnector(String id, String description) {
        super(id, description);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBatched() {
        return true;
    }
}
