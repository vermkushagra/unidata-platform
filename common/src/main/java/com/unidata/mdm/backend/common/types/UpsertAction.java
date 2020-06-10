package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * The actual action type, that was carried out.
 */
public enum UpsertAction {
    /**
     * Actual action was insert.
     */
    INSERT,
    /**
     * Actual action was update.
     */
    UPDATE,
    /**
     * No action was actually performed.
     */
    NO_ACTION;
}
