/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * @author Mikhail Mikhailov
 *
 */
public class TimeIntervalDTO {

    /**
     * Contributors.
     */
    private final List<ContributorDTO> contributors = new ArrayList<>(4);
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
     * @param validFrom period's validity start timestamp
     * @param validTo period's validity end timestamp
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

    /**
     * Tells whether the given date is within range of this time interval.
     * @param asOf the date to check interval against. Must not be null
     * @return true, if included, false otherwise
     */
    public boolean isInRange(@Nonnull Date asOf) {
        boolean left = validFrom == null || validFrom.before(asOf) || validFrom.getTime() == asOf.getTime();
        boolean right = validTo == null || validTo.after(asOf) || validTo.getTime() == asOf.getTime();
        return left && right;
    }
}