package com.unidata.mdm.backend.common.dto;

import com.unidata.mdm.backend.common.types.EtalonClassifier;

/**
 * @author Mikhail Mikhailov
 * Etalon classifier data DTO.
 */
public interface EtalonClassifierDTO {
    /**
     * Gets the etalon data.
     * @return etalon data or null
     */
    EtalonClassifier getEtalon();
}
