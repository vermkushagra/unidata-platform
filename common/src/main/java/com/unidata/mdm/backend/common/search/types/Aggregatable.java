package com.unidata.mdm.backend.common.search.types;

/**
 * @author Mikhail Mikhailov
 * Aggregatable result.
 */
public interface Aggregatable {
    /**
     * Discard by collection or nor.
     * @return true / false
     */
    boolean discard();
    /**
     * Stop aggregating.
     * @return true / false
     */
    boolean stop();
}
