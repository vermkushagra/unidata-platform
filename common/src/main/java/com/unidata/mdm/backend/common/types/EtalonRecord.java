package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * The etalon record.
 */
public interface EtalonRecord extends DataRecord, CalculationResult<OriginRecord> {
    /**
     * Gets the info section.
     * @return info section
     */
    EtalonRecordInfoSection getInfoSection();
}
