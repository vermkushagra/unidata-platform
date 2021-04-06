package com.unidata.mdm.backend.service.data.batch;

/**
 * @author Mikhail Mikhailov
 * Record batch targets.
 */
public enum BatchTarget {
    /**
     * Etalon insert target.
     */
    ETALON_INSERTS,
    /**
     * Etalon update target.
     */
    ETALON_UPDATES,
    /**
     * Origin insert target.
     */
    ORIGIN_INSERTS,
    /**
     * Origin update target.
     */
    ORIGIN_UPDATES,
    /**
     * Vistory.
     */
    VISTORY
}
