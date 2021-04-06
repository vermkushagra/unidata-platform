package com.unidata.mdm.backend.service.data.batch;

/**
 * @author Mikhail Mikhailov
 * Set is considered large from 10 mio upwards.
 */
public enum BatchSetSize {
    SMALL,
    LARGE
}
