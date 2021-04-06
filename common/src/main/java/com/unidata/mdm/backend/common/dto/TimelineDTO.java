/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail Mikhailov
 *
 */
public class TimelineDTO {

    /**
     * Etalon id.
     */
    private final String etalonId;

    /**
     * Intervals.
     */
    private final List<TimeIntervalDTO> intervals = new ArrayList<>();

    /**
     * Constructor.
     */
    public TimelineDTO(String etalonId) {
        super();
        this.etalonId = etalonId;
    }

    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }


    /**
     * @return the intervals
     */
    public List<TimeIntervalDTO> getIntervals() {
        return intervals;
    }

}
