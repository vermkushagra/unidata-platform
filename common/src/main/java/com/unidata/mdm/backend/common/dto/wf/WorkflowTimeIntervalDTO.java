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
    private final List<ContributorDTO> pendings = new ArrayList<>();

    /**
     * Interval is in pending state.
     */
    private final boolean pending;

    /**
     * Constructor.
     * @param period's validity start timestamp
     * @param period's validity end timestamp
     * @param periodId period id (index onn the time line)
     * @param isActive activity mark
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
     * Gets deleted state ->
     *
     * @return true if interval was "hard" deleted, otherwise false
     */
    public boolean isDeleted() {
        return !isActive() && !isPending();
    }
}
