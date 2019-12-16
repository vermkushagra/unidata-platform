package org.unidata.mdm.system.type.pipeline;

/**
 * @author Mikhail Mikhailov on Oct 2, 2019
 */
public abstract class Finish<C extends PipelineInput, R extends PipelineOutput> extends Segment {
    /**
     * The exact output type class.
     */
    private final Class<R> outputTypeClass;
    /**
     * Constructor.
     * @param id
     * @param description
     */
    public Finish(String id, String description, Class<R> outputTypeClass) {
        super(id, description);
        this.outputTypeClass = outputTypeClass;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public SegmentType getType() {
        return SegmentType.FINISH;
    }
    /**
     * @return the outputTypeClass
     */
    public Class<R> getOutputTypeClass() {
        return outputTypeClass;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBatched() {
        return false;
    }
    /**
     * Preformes the last pipeline step, converting context state to result.
     * @param ctx the context
     * @return result
     */
    public abstract R finish(C ctx);
}
