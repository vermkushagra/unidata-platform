package com.unidata.mdm.backend.common.cleanse;

/**
 * @author Mikhail Mikhailov
 * This is the counterpart of UPath filtering enum, controlling value filetring mode for a port.
 */
public enum CleanseFunctionPortFilteringMode {
    /**
     * Apply to all (collect all hits).
     */
    MODE_ALL,
    /**
     * Apply to all (collect all hits) and collect also incomplete paths.
     */
    MODE_ALL_WITH_INCOMPLETE,
    /**
     * Apply once (collect / set to first and exit).
     */
    MODE_ONCE,
    /**
     * Doesn't apply or undefined.
     */
    MODE_UNDEFINED;
}
