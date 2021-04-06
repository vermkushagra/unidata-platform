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
}
