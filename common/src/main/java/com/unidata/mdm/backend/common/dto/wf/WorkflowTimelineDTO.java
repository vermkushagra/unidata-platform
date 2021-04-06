/**
 *
 */
package com.unidata.mdm.backend.common.dto.wf;

import com.unidata.mdm.backend.common.dto.TimelineDTO;

/**
 * @author mikhail
 * Timeline extended with workflow elements.
 */
public class WorkflowTimelineDTO extends TimelineDTO {

    /**
     * Has pending versions
     */
    private final boolean pending;
    /**
     * Has approved versions.
     */
    private final boolean published;
    /**
     * Constructor.
     * @param etalonId the etalon id
     * @param pending timeline is in pending state
     * @param published timeline has approved versions (was published once)
     */
    public WorkflowTimelineDTO(String etalonId, boolean pending, boolean published) {
        super(etalonId);
        this.pending = pending;
        this.published = published;
    }
    /**
     * Timeline in pending state or not.
     * @return true if so, false otherwise
     */
    public boolean isPending() {
        return pending;
    }

    /**
     * Tells whether this timeline is published or not.
     * @return true if so, false otherwise
     */
    public boolean isPublished() {
        return published;
    }
}
