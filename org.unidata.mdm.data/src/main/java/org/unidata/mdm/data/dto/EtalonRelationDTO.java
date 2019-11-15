package org.unidata.mdm.data.dto;

import org.unidata.mdm.data.type.data.EtalonRelation;

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
