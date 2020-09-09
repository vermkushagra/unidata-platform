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
