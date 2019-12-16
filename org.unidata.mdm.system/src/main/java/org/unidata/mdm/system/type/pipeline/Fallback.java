package org.unidata.mdm.system.type.pipeline;

import java.util.function.BiConsumer;

/**
 * @author Alexander Malyshev
 */
public abstract class Fallback<C extends PipelineInput> extends Segment implements BiConsumer<C, Throwable> {
    /**
     * Constructor.
     * @param id
     * @param description
     */
    public Fallback(String id, String description) {
        super(id, description);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBatched() {
        return false;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public SegmentType getType() {
        return SegmentType.FALLBACK;
    }
}
