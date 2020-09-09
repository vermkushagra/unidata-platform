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

package com.unidata.mdm.backend.api.rest.dto.cleanse;

import java.util.List;


/**
 * The Class CFCompositeResponse.
 */
public class CFCompositeResponse {
    
    /** The status. */
    private CFSaveStatus status;
    
    /** The circuits. */
    private List<List<CFLink>> cycles;

    /**
     * Gets the status.
     *
     * @return the status
     */
    public CFSaveStatus getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(CFSaveStatus status) {
        this.status = status;
    }

    /**
     * Gets the circuits.
     *
     * @return the circuits
     */
    public List<List<CFLink>> getCycles() {
        return cycles;
    }

    /**
     * Sets the circuits.
     *
     * @param cycles the new circuits
     */
    public void setCycles(List<List<CFLink>> cycles) {
        this.cycles = cycles;
    }
}
