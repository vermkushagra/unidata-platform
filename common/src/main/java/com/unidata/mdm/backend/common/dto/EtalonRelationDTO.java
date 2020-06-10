package com.unidata.mdm.backend.common.dto;

import com.unidata.mdm.backend.common.types.EtalonRelation;

/**
 * @author Mikhail Mikhailov
 * Etalon relation DTO.
 */
public interface EtalonRelationDTO {

    /**
     * Gets the relation object.
     * @return relation object
     */
    EtalonRelation getEtalon();
}
