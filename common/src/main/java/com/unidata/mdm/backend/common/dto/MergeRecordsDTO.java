package com.unidata.mdm.backend.common.dto;


import java.util.List;

/**
 * @author Mikhail Mikhailov
 *
 */
public class MergeRecordsDTO {

    /**
     * Operation successful, winner id is set.
     */
    private final String winnerId;

    private final List<String> mergedIds;

    private List<ErrorInfoDTO> errors;

    /**
     * Constructor.
     */
    public MergeRecordsDTO(String winnerId, List<String> mergedIds) {
        super();
        this.winnerId = winnerId;
        this.mergedIds = mergedIds;
    }

    /**
     * @return the winnerId
     */
    public String getWinnerId() {
        return winnerId;
    }

    public List<String> getMergedIds() {
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
}
