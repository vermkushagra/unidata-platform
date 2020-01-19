package org.unidata.mdm.system.type.pipeline.batch;

import org.unidata.mdm.system.type.pipeline.Fallback;

/**
 * @author Alexander Malyshev
 */
public abstract class BatchedFallback<C extends BatchedPipelineInput> extends Fallback<C> {
    /**
     * Constructor.
     * @param id
     * @param description
     */
    public BatchedFallback(String id, String description) {
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
