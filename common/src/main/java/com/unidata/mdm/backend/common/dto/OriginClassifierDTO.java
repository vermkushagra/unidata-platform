package com.unidata.mdm.backend.common.dto;

import java.util.List;

import com.unidata.mdm.backend.common.types.OriginClassifier;

/**
 * @author Mikhail Mikhailov
 * Gets origins for a classifier object.
 */
public interface OriginClassifierDTO {
    /**
     * Gets the origins classifier records.
     * @return list of records.
     */
    List<OriginClassifier> getOrigins();
}
