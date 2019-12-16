package org.unidata.mdm.system.type.pipeline.batch;

import org.unidata.mdm.system.type.pipeline.PipelineInput;
import org.unidata.mdm.system.type.pipeline.Point;

/**
 * @author Mikhail Mikhailov
 * Batched point segment.
 */
public abstract class BatchedPoint<C extends PipelineInput> extends Point<C> {
    /**
     * Constructor.
     * @param id the id
     * @param description the description
     */
    public BatchedPoint(String id, String description) {
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
