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
