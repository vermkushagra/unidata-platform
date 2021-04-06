package com.unidata.mdm.backend.exchange.def;

/**
 * @author Mikhail Mikhailov
 * Type of the update mark.
 */
public enum UpdateMarkType {
    /**
     * Will read from / write to the last update time stamp.
     */
    TIMESTAMP,
    /**
     * Will read / write bollean value 'true' to mark a record as updated.
     */
    BOOLEAN
}
