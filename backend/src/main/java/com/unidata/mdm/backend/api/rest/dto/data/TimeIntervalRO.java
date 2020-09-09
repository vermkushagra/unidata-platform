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
package com.unidata.mdm.backend.api.rest.dto.data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Mikhail Mikhailov
 * Time interval REST object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeIntervalRO {
    /**
     * Date from.
     */
    private LocalDateTime dateFrom;

    /**
     * Date to.
     */
    private LocalDateTime dateTo;

    /**
     * Is this interval active or not.
     */
    private boolean active;

    /**
     * Contributors.
     */
    private List<ContributorRO> contributors;

    /**
     * Constructor.
     */
    public TimeIntervalRO() {
        super();
    }


    /**
     * @return the dateFrom
     */
    public LocalDateTime getDateFrom() {
        return dateFrom;
    }


    /**
     * @param dateFrom the dateFrom to set
     */
    public void setDateFrom(LocalDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }


    /**
     * @return the dateTo
     */
    public LocalDateTime getDateTo() {
        return dateTo;
    }


    /**
     * @param dateTo the dateTo to set
     */
    public void setDateTo(LocalDateTime dateTo) {
        this.dateTo = dateTo;
    }


    /**
     * @return the contributors
     */
    public List<ContributorRO> getContributors() {
        return contributors;
    }


    /**
     * @param contributors the contributors to set
     */
    public void setContributors(List<ContributorRO> contributors) {
        this.contributors = contributors;
    }



    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }



    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

}
