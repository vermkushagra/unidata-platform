package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * The etalon record.
 */
public interface EtalonRecord extends DataRecord {
    /**
     * Gets the info section.
     * @return info section
     */
    EtalonRecordInfoSection getInfoSection();
}
