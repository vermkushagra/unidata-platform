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

package com.unidata.mdm.backend.service.job.softdeletecleanup;

import com.unidata.mdm.backend.service.job.common.StepExecutionState;

/**
 * @author Dmitrii Kopin
 */
public class SoftDeleteCleanupStepExecutionState implements StepExecutionState {

    private long processedRecords = 0L;
    private long failedRecords = 0L;
    private long deleteRecords = 0L;

    /**
     * Constructor.
     */
    public SoftDeleteCleanupStepExecutionState() {
        super();
    }

    /**
     * @param processedRecords the processedRecords to set
     */
    public void incrementProcessedRecords(long processedRecords) {
        this.processedRecords += processedRecords;
    }

    /**
     * @param failedRecords the failedRecords to set
     */
    public void incrementFailedRecords(long failedRecords) {
        this.failedRecords += failedRecords;
    }

    /**
     * @param deleteRecords the deleteRecords to set
     */
    public void incrementDeleteRecords(long deleteRecords) {
        this.deleteRecords += deleteRecords;
    }

    /**
     * get failed records count
     *
     * @return records count
     */
    public long getFailedRecords() {
        return failedRecords;
    }

    /**
     * get processed records
     *
     * @return records count
     */
    public long getProcessedRecords() {
        return processedRecords;
    }

    /**
     * get delete records
     *
     * @return records count
     */
    public long getDeleteRecords() {
        return deleteRecords;
    }
}
