package org.unidata.mdm.system.type.pipeline;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.unidata.mdm.system.context.PipelineExecutionContext;

/**
 * @author Mikhail Mikhailov
 * The pipeline starting segment.
 */
public abstract class Start<C extends PipelineExecutionContext> extends Segment {
    /**
     * The input type class.
     */
    private final Class<C> inputTypeClass;
    /**
     * Constructor.
     * @param id the id
     * @param description the description
     * @param inputTypeClass the input type class
     */
    public Start(String id, String description, Class<C> inputTypeClass) {
        super(id, description);
        this.inputTypeClass = inputTypeClass;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public SegmentType getType() {
        return SegmentType.START;
    }
    /**
     * @return the inputTypeClass
     */
    public Class<C> getInputTypeClass() {
        return inputTypeClass;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return start == this;
    }
    /**
     * Performs the step.
     * @param ctx the context
     */
    public abstract void start(@Nonnull C ctx);
    /**
     * FIXME Refactor! This approach won't work for pipeline selection.
     *
     * Selects a pre-configured or default pipeline for the supplied context.
     * @param ctx the context to use
     * @return pipeline instance or null
     */
    @Nullable
    public abstract Pipeline select(C ctx);
}
