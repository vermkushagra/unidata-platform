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

package org.unidata.mdm.data.dto;


import java.util.List;

import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * @author Mikhail Mikhailov
 *
 */
public class MergeRecordsDTO implements PipelineOutput {

    /**
     * Operation successful, winner id is set.
     */
    private final RecordKeys winnerId;

    private final List<RecordKeys> mergedIds;

    private List<ErrorInfoDTO> errors;

    private boolean mergeWithConflicts = false;

    /**
     * Constructor.
     */
    public MergeRecordsDTO(RecordKeys winnerId, List<RecordKeys> mergedIds) {
        super();
        this.winnerId = winnerId;
        this.mergedIds = mergedIds;
    }

    /**
     * @return the winnerId
     */
    public RecordKeys getWinnerId() {
        return winnerId;
    }

    public List<RecordKeys> getMergedIds() {
        return mergedIds;
    }

    /**
     * list of errors
     */
    public List<ErrorInfoDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorInfoDTO> errors) {
        this.errors = errors;
    }

    public boolean isMergeWithConflicts() {
        return mergeWithConflicts;
    }

    public void setMergeWithConflicts(boolean mergeWithConflicts) {
        this.mergeWithConflicts = mergeWithConflicts;
    }
}
