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
package com.unidata.mdm.backend.common.dto.wf;

import com.unidata.mdm.backend.common.dto.TimelineDTO;

/**
 * @author mikhail
 * Timeline extended with workflow elements.
 * TODO: Move timeline and interval code to internal types. Simplify and separate a new DTO objects.
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
