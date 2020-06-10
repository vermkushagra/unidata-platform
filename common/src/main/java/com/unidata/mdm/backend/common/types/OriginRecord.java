package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * The origin record.
 */
public interface OriginRecord extends DataRecord, Calculable {
    /**
     * Gets the info section.
     * @return info section
     */
    OriginRecordInfoSection getInfoSection();
}
