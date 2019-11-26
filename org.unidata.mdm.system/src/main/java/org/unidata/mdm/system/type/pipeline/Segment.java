package org.unidata.mdm.system.type.pipeline;

import javax.annotation.Nullable;

/**
 * @author Mikhail Mikhailov
 * Segment (integration point)
 */
public abstract class Segment {
    /**
     * Segment id.
     */
    private final String id;
    /**
     * Segment descripton.
     */
    private final String description;

    /**
     * Constructor.
     * @param id the ID
     * @param description the description
     */
    public Segment(String id, String description) {
        super();
        this.id = id;
        this.description = description;
    }
    /**
     * Gets the segment ID. Must be unique accross the system.
     * @return ID
     */
    public String getId() {
        return id;
    }
    /**
     * Gets type description.
     * @return description
     */
    public String getDescription() {
        return description;
    }
    /**
     * Gets the segment type.
     * @return type
     */
    public abstract SegmentType getType();
    /**
     * Check for supporting of pipelines, initiated by this starting point.
     * @param start the starting point
     * @return true, if supports, false otherwise
     */
    public abstract boolean supports(Start<?> start);

    /**
     * Gets the segment fallback function.
     * @return fallback
     */
    @Nullable
    public Fallback fallback() {
        return null;
    }
}
