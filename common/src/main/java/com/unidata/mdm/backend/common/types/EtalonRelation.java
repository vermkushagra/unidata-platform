package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 *
 */
public interface EtalonRelation extends DataRecord, CalculationResult<OriginRelation> {
    /**
     * Gets the info section.
     * @return the info section.
     */
    EtalonRelationInfoSection getInfoSection();
}
