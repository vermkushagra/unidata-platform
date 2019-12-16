package org.unidata.mdm.system.type.pipeline;

/**
 * @author Mikhail Mikhailov
 * Point segment.
 */
public abstract class Point<C extends PipelineInput> extends Segment {
    /**
     * Constructor.
     * @param id the id
     * @param description the description
     */
    public Point(String id, String description) {
        super(id, description);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public SegmentType getType() {
        return SegmentType.POINT;
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
    public abstract void point(C ctx);
}
