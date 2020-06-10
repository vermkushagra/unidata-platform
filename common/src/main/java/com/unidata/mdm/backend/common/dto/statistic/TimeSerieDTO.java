package com.unidata.mdm.backend.common.dto.statistic;

import java.util.Date;

/**
 * The Class TimeSerie.
 */
public class TimeSerieDTO {
    
    /** The time. */
    private Date time;
    
    /** The value. */
    private int value;
    
    /**
     * Gets the time.
     *
     * @return the time
     */
    public Date getTime() {
        return time;
    }
    
    /**
     * Sets the time.
     *
     * @param time the new time
     */
    public void setTime(Date time) {
        this.time = time;
    }
    
    /**
     * Gets the value.
     *
     * @return the value
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(int value) {
        this.value = value;
    }
}
