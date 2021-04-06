/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mikhail Mikhailov
 *
 */
public class TimeIntervalDTO {

    /**
     * Contributors.
     */
    private final List<ContributorDTO> contributors = new ArrayList<>();
    /**
     * Valid from.
     */
    private final Date validFrom;
    /**
     * Valid to.
     */
    private final Date validTo;
    /**
     * Interval is active or not.
     */
    private final boolean active;
    /**
     * Period id (index onn the time line).
     */
    private final long periodId;
    /**
     * Constructor.
     * @param period's validity start timestamp
     * @param period's validity end timestamp
     * @param periodId period id (index onn the time line)
     * @param isActive activity mark
     */
    public TimeIntervalDTO(Date validFrom, Date validTo, long periodId, boolean isActive) {
        super();
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.periodId = periodId;
        this.active = isActive;
    }

    /**
     * @return the contributors
     */
    public List<ContributorDTO> getContributors() {
        return contributors;
    }

    /**
     * @return the validFrom
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @return the validTo
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * @return the periodId
     */
    public long getPeriodId() {
        return periodId;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

}
