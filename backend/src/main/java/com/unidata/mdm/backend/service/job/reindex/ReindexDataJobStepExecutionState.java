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

package com.unidata.mdm.backend.service.job.reindex;

import com.unidata.mdm.backend.service.job.common.StepExecutionState;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ReindexDataJobStepExecutionState implements StepExecutionState {
    /**
     * Records.
     */
    private long reindexedRecords = 0L;
    /**
     * Classified records.
     */
    private long classifiedRecords = 0L;
    /**
     * Classifier data records.
     */
    private long reindexedClassifiers = 0L;
    /**
     * Inserted.
     */
    private long reindexedRelations = 0L;
    /**
     * Constructor.
     */
    public ReindexDataJobStepExecutionState() {
        super();
    }
    /**
     * @param reindexedRecords the reindexedRecords to set
     */
    public void incrementReindexedRecords(long reindexedRecords) {
        this.reindexedRecords += reindexedRecords;
    }
    /**
     * @param reindexedClassifiers the reindexedClassifiers to set
     */
    public void incrementReindexedClassifiers(long reindexedClassifiers) {
        this.reindexedClassifiers += reindexedClassifiers;
    }
    /**
     * @param classifiedRecords the classifiedRecords to set
     */
    public void incrementClassifiedRecords(long classifiedRecords) {
        this.classifiedRecords += classifiedRecords;
    }
    /**
     * @param reindexedRelations the reindexedRelations to set
     */
    public void incrementReindexedRelations(long reindexedRelations) {
        this.reindexedRelations += reindexedRelations;
    }
    /**
     * @return the failed
     */
    public long getReindexedRecords() {
        return reindexedRecords;
    }
    /**
     * @return the skept
     */
    public long getReindexedClassifiers() {
        return reindexedClassifiers;
    }
    /**
     * @return the updated
     */
    public long getClassifiedRecords() {
        return classifiedRecords;
    }
    /**
     * @return the inserted
     */
    public long getReindexedRelations() {
        return reindexedRelations;
    }
}
