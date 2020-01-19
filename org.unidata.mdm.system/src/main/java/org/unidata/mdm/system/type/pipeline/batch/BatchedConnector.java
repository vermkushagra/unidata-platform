package org.unidata.mdm.system.type.pipeline.batch;

import org.unidata.mdm.system.type.pipeline.Connector;

/**
 * @author Mikhail Mikhailov
 * Batched connector segment.
 */
public abstract class BatchedConnector<I extends BatchedPipelineInput, O extends BatchedPipelineOutput> extends Connector<I, O> {
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
