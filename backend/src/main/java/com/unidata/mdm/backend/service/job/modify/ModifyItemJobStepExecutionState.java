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

package com.unidata.mdm.backend.service.job.modify;

import com.unidata.mdm.backend.service.job.common.StepExecutionState;

/**
 * @author Dmitrii Kopin
 *
 */
public class ModifyItemJobStepExecutionState implements StepExecutionState {

    private long modifyRecords = 0L;
    private long failedRecords = 0L;
    private long skeptRecords = 0L;
    private long modifyClassifiers = 0L;
    private long skeptClassifiers = 0L;
    private long failedClassifiers = 0L;
    private long deletedClassifiers = 0L;
    private long modifyRelations = 0L;
    private long skeptRelations = 0L;
    private long deletedRelations = 0L;
    private long failedRelations = 0L;
    /**
     * Constructor.
     */
    public ModifyItemJobStepExecutionState() {
        super();
    }
    /**
     * @param modifyRecords the modifyRecords to set
     */
    public void incrementModifyRecords(long modifyRecords) {
        this.modifyRecords += modifyRecords;
    }
    /**
     * @param modifyRelations the modifyRelations to set
     */
    public void incrementModifyRelations(long modifyRelations) {
        this.modifyRelations += modifyRelations;
    }
    /**
     * @param modifyClassifiers the modifyClassifiers to set
     */
    public void incrementModifyClassifiers(long modifyClassifiers) {
        this.modifyClassifiers += modifyClassifiers;
    }

    /**
     * @param skeptRecords the skeptRecords to set
     */
    public void incrementSkeptRecords(long skeptRecords) {
        this.skeptRecords += skeptRecords;
    }
    /**
     * @param skeptRelations the skeptRelations to set
     */
    public void incrementSkeptRelations(long skeptRelations) {
        this.skeptRelations += skeptRelations;
    }
    /**
     * @param skeptClassifiers the skeptClassifiers to set
     */
    public void incrementSkeptClassifiers(long skeptClassifiers) {
        this.skeptClassifiers += skeptClassifiers;
    }
    /**
     * @param deletedClassifiers the deletedClassifiers to set
     */
    public void incrementDeletedRecords(long deletedClassifiers) {
        this.deletedClassifiers += deletedClassifiers;
    }

    /**
     * @param deletedRelations the deletedRelations to set
     */
    public void incrementDeletedRelations(long deletedRelations) {
        this.deletedRelations += deletedRelations;
    }

    /**
     * @param failedRecords the failedRecords to set
     */
    public void incrementFailedRecords(long failedRecords) {
        this.failedRecords += failedRecords;
    }
    /**
     * @param failedRelations the failedRelations to set
     */
    public void incrementFailedRelations(long failedRelations) {
        this.failedRelations += failedRelations;
    }
    /**
     * @param failedClassifiers the failedClassifiers to set
     */
    public void incrementFailedClassifiers(long failedClassifiers) {
        this.failedClassifiers += failedClassifiers;
    }

    /**
     * Records.
     */
    public long getModifyRecords() {
        return modifyRecords;
    }

    /**
     * Classified records.
     */
    public long getModifyClassifiers() {
        return modifyClassifiers;
    }

    /**
     * Modify relations.
     */
    public long getSkeptRelations() {
        return skeptRelations;
    }

    /**
     * Skept Records.
     */
    public long getSkeptRecords() {
        return skeptRecords;
    }

    /**
     * Skept Classified records.
     */
    public long getSkeptClassifiers() {
        return skeptClassifiers;
    }

    /**
     * Skept Modify relations.
     */
    public long getModifyRelations() {
        return modifyRelations;
    }


    /**
     * Delete relations.
     */
    public long getDeletedRelations() {
        return deletedRelations;
    }

    public long getFailedRecords() {
        return failedRecords;
    }

    public long getFailedClassifiers() {
        return failedClassifiers;
    }

    public long getFailedRelations() {
        return failedRelations;
    }

    public long getDeletedClassifiers() {
        return deletedClassifiers;
    }
}
