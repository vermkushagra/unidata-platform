/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Time line REST object.
 */
public class TimelineRO {
    /**
     * Etalon ID.
     */
    private String etalonId;

    /**
     * Time line.
     */
    private List<TimeIntervalRO> timeline = new ArrayList<>();

    /**
     * Constructor.
     */
    public TimelineRO() {
        super();
    }

    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @param etalonId the etalonId to set
     */
    public void setEtalonId(String etalonId) {
        this.etalonId = etalonId;
    }



    /**
     * @return the timeline
     */
    public List<TimeIntervalRO> getTimeline() {
        return timeline;
    }



    /**
     * @param timeline the timeline to set
     */
    public void setTimeline(List<TimeIntervalRO> timeline) {
        this.timeline = timeline;
    }

}
