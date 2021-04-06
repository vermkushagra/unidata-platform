package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 *
 */
public interface EtalonRelation extends DataRecord {
    /**
     * Gets the info section.
     * @return the info section.
     */
    EtalonRelationInfoSection getInfoSection();
}
