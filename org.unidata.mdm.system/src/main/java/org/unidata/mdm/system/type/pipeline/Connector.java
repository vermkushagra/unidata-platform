package org.unidata.mdm.system.type.pipeline;

import org.unidata.mdm.system.type.pipeline.fragment.InputFragmentContainer;

/**
 * @author Mikhail Mikhailov
 * Connector segment marker interface.
 */
public abstract class Connector<I extends PipelineInput, O extends PipelineOutput> extends Segment {
    /**
     * Constructor.
     * @param id the id
     * @param description the description
     */
    public Connector(String id, String description) {
        super(id, description);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public SegmentType getType() {
        return SegmentType.CONNECTOR;
    }
    /**
     * Default implementation, that just checks the input type for being a composite context.
     * Probably should almost always be overridden in subclasses.
     */
    @Override
    public boolean supports(Start<?> start) {
        return InputFragmentContainer.class.isAssignableFrom(start.getInputTypeClass());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBatched() {
        return false;
    }
    /**
     * Performs actual execution of the segment's code.
     * @param ctx the context to execute the segment on
     */
    public abstract O connect(I ctx);
    /**
     * Performs actual execution of the segment's code but using the supplied pipeline.
     * @param ctx the context to execute the segment on
     * @param p the supplied pipeline
     */
    public abstract O connect(I ctx, Pipeline p);
}
