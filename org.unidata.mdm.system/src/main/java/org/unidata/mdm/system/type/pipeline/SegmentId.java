package org.unidata.mdm.system.type.pipeline;

import java.util.Objects;

/**
 * The segment id type.
 * @author Mikhail Mikhailov on Nov 13, 2019
 */
public final class SegmentId<T extends Segment> {
    /**
     * This segment string id.
     */
    private final String id;
    /**
     * Segment description.
     */
    private final String description;
    /**
     * The link to segment.
     */
    private final T segment;
    /**
     * Constructor.
     */
    public SegmentId(String id, String description, T segment) {
        super();

        Objects.requireNonNull(id, "Segment id must not be null.");
        Objects.requireNonNull(description, "Segment description must not be null.");
        Objects.requireNonNull(segment, "Segment instance must not be null.");

        this.id = id;
        this.description = description;
        this.segment = segment;
    }
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @return the segment
     */
    public T getSegment() {
        return segment;
    }
}
