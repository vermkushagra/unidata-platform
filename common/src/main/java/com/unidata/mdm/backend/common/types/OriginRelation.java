package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * The origin relation.
 */
public interface OriginRelation extends DataRecord, Calculable {
    /**
     * Gets the info section.
     * @return the info section
     */
    OriginRelationInfoSection getInfoSection();
}
