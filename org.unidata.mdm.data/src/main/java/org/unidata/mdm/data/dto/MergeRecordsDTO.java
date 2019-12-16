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
