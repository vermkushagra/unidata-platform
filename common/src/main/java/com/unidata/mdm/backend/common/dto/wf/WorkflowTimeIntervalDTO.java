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

package com.unidata.mdm.backend.common.dto.wf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.dto.ContributorDTO;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;

/**
 * @author mikhail
 * Period workflow state.
 */
public class WorkflowTimeIntervalDTO extends TimeIntervalDTO {

    /**
     * Contributors.
     */
    private final List<ContributorDTO> pendings = new ArrayList<>(4);

    /**
     * Interval is in pending state.
     */
    private final boolean pending;

    /**
     * Constructor.
     * @param validFrom period's validity start timestamp
     * @param validTo period's validity end timestamp
     * @param periodId period id (index onn the time line)
     * @param active activity mark
     * @param pending wither the period is in pending state
     */
    public WorkflowTimeIntervalDTO(Date validFrom, Date validTo, long periodId, boolean active, boolean pending) {
        super(validFrom, validTo, periodId, active);
        this.pending = pending;
    }

    /**
     * Gets pending versions.
     * @return pendings
     */
    public List<ContributorDTO> getPendings() {
        return pendings;
    }

    /**
     * Gets pending state
     * @return boolean
     */
    public boolean isPending() {
        return pending;
    }

    /**
     * Gets deleted state -&gt;
     *
     * @return true if interval was "hard" deleted, otherwise false
     */
    public boolean isDeleted() {
        return !isActive() && !isPending();
    }
}
